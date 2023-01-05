package tasuku;

import info.clearthought.layout.TableLayout;
import org.apache.commons.io.FilenameUtils;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class TaskSelectorUi extends JFrame implements TreeSelectionListener, ActionListener {
  private final Workspace workspace;
  private final WorkspaceModel model;
  private JTextField taskNameTextField;
  private JButton executeButton;
  private JButton copyToClipboardButton;
  public Optional<String> selectedTaskName = Optional.empty();
  public final CountDownLatch latch;

  public TaskSelectorUi(Workspace workspace) {
    super("Task Selector UI");

    this.latch = new CountDownLatch(1);
    this.workspace = workspace;
    this.model = new WorkspaceModel(workspace);
    JTree tree = new JTree();
    tree.setModel(model);
    tree.addTreeSelectionListener(this);
    JScrollPane scrollPane = new JScrollPane(tree);

    double tableLayoutSizes[][] =
        {
            {
                TableLayout.FILL,
                TableLayout.MINIMUM,
                TableLayout.MINIMUM,
            },
            {
                TableLayout.MINIMUM,
            }
        };
    TableLayout tableLayout = new TableLayout(tableLayoutSizes);

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(tableLayout);

    taskNameTextField = new JTextField();
    taskNameTextField.setEnabled(false);
    controlPanel.add(taskNameTextField, "0,0,0,0");

    copyToClipboardButton = new JButton("Copy Name!");
    controlPanel.add(copyToClipboardButton, "1,0,1,0");
    copyToClipboardButton.addActionListener(this);
    copyToClipboardButton.setEnabled(false);

    executeButton = new JButton("Execute!");
    controlPanel.add(executeButton, "2,0,2,0");
    executeButton.addActionListener(this);
    executeButton.setEnabled(false);

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        latch.countDown();
      }
    });

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(controlPanel, BorderLayout.SOUTH);
    setSize(400, 600);
  }

  public static Optional<String> run(Workspace workspace) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    TaskSelectorUi frame = new TaskSelectorUi(workspace);
    frame.setVisible(true);
    try {
      frame.latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return frame.selectedTaskName;
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    TaskNode node = (TaskNode) e.getPath().getLastPathComponent();
    if (workspace.taskExists(node.taskName)) {
      taskNameTextField.setText(node.taskName);
      executeButton.setEnabled(true);
      copyToClipboardButton.setEnabled(true);
    } else {
      taskNameTextField.setText("");
      executeButton.setEnabled(false);
      copyToClipboardButton.setEnabled(false);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == executeButton) {
      setVisible(false);
      selectedTaskName = Optional.of(taskNameTextField.getText());
      dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    } else if (e.getSource() == copyToClipboardButton) {
      StringSelection selection = new StringSelection(taskNameTextField.getText());
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(selection, selection);
    }
  }

  static class TaskNode {
    final String taskName;
    final String displayName;
    final HashSet<String> childrenSet = new HashSet<>();
    final ArrayList<String> childrenList = new ArrayList<>();

    TaskNode(String taskName) {
      this.taskName = taskName;
      this.displayName = FilenameUtils.getName(taskName);
    }

    TaskNode(String taskName, String displayName) {
      this.taskName = taskName;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  static class WorkspaceModel implements TreeModel {
    private final Workspace workspace;
    private final HashMap<String, TaskNode> nodes = new HashMap<>();

    WorkspaceModel(Workspace workspace) {
      this.workspace = workspace;
      TaskNode theRoot = new TaskNode("//", "Tasks");
      nodes.put("//", theRoot);

      for (String taskName : workspace.getTaskNames()) {
        String[] components = taskName.split("/");
        String nodeName = "//";
        for (int i = 0; i < components.length; i++) {
          String parentName = nodeName;
          if (i == 0) {
            nodeName = components[i];
          } else {
            nodeName = nodeName + "/" + components[i];
          }
          TaskNode parent = getNode(parentName);
          TaskNode node = getNode(nodeName);
          parent.childrenSet.add(node.taskName);
        }
      }

      for (Map.Entry<String, TaskNode> entry : nodes.entrySet()) {
        TaskNode node = entry.getValue();
        ArrayList<String> hasChildren = new ArrayList<>();
        ArrayList<String> noChildren = new ArrayList<>();
        for (String childName : node.childrenSet) {
          TaskNode child = nodes.get(childName);
          if (child.childrenSet.size() == 0) {
            noChildren.add(child.taskName);
          } else {
            hasChildren.add(child.taskName);
          }
        }
        Collections.sort(hasChildren);
        Collections.sort(noChildren);
        node.childrenList.addAll(hasChildren);
        node.childrenList.addAll(noChildren);
      }
    }

    private TaskNode getNode(String nodeName) {
      if (nodes.containsKey(nodeName)) {
        return nodes.get(nodeName);
      } else {
        TaskNode node = new TaskNode(nodeName);
        nodes.put(nodeName, node);
        return node;
      }
    }


    @Override
    public Object getRoot() {
      return nodes.get("//");
    }

    @Override
    public Object getChild(Object parent, int index) {
      TaskNode node = (TaskNode) parent;
      return nodes.get(node.childrenList.get(index));
    }

    @Override
    public int getChildCount(Object parent) {
      TaskNode node = (TaskNode) parent;
      return node.childrenSet.size();
    }

    @Override
    public boolean isLeaf(Object node) {
      return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
      // NO-OP
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
      TaskNode parentNode = (TaskNode) parent;
      TaskNode childNode = (TaskNode) child;
      return parentNode.childrenList.indexOf(childNode.taskName);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
      // NO-OP
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
      // NO-OP
    }
  }
}
