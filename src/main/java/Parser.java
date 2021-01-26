import java.time.LocalDate;
import java.util.ArrayList;

// DEALS WITH MAKING SENSE OF USER'S COMMAND
public class Parser {

    protected Storage storage;
    protected Ui ui;
    protected TaskList tasks;

    public Parser(Storage storage, Ui ui, TaskList tasks) {
        this.storage = storage;
        this.ui = ui;
        this.tasks = tasks;
    }

    static String parseCommandType(String command) {
        String[] tokenizedCommand = command.split(" ");
        return tokenizedCommand[0];
    }

    static int parseTaskIndex(String command) {
        String[] tokenizedCommand = command.split(" ");
        return Integer.parseInt(tokenizedCommand[1]);
    }

    static String parseTodoDescription(String command) {
        return command.substring(5);
    }

    static String parseDeadlineDescription(String command) {
        return command.substring(9).split(" /by ")[0];
    }

    static LocalDate parseDeadlineDate(String command) {
        return LocalDate.parse(command.substring(9).split(" /by ")[1]);
    }

    static String parseEventDescription(String command) {
        return command.substring(6).split(" /at ")[0];
    }

    static String parseEventDate(String command) {
        return command.substring(6).split(" /at ")[1];
    }

    static String parseKeyword(String command) {
        return command.substring(5);
    }

    static boolean hasExited(String command) {
        return command.equals("bye");
    }

    public void parseCommand(String command) {
        String commandType = Parser.parseCommandType(command);
        try {
            if (commandType.equals("list")) {
                // SHOW LIST
                this.ui.showList(tasks.list);
            } else if (commandType.equals("done")) {
                // MARK TASK AS COMPLETE
                int taskIndex = Parser.parseTaskIndex(command) - 1;
                Task completedTask = tasks.list.get(taskIndex);
                completedTask.markAsDone();
                ui.showDone(completedTask);
            } else if (commandType.equals("todo")) {
                // ADD TODO TASK
                try {
                    String description = Parser.parseTodoDescription(command);
                    Task task = new Todo(description);
                    tasks.addTask(task);
                    ui.showAddTask(tasks.list);
                } catch (StringIndexOutOfBoundsException e) {
                    ui.showInvalidTodo();
                }
            } else if (commandType.equals("deadline")) {
                // ADD DEADLINE TASK
                String deadlineDescription = Parser.parseDeadlineDescription(command);
                LocalDate deadlineDate = Parser.parseDeadlineDate(command); // INPUT DATE IS YYYY-MM-DD
                Task task = new Deadline(deadlineDescription, deadlineDate);
                tasks.addTask(task);
                ui.showAddTask(tasks.list);
            } else if (commandType.equals("event")) {
                // ADD EVENT TASK
                String eventDescription = Parser.parseEventDescription(command);
                String eventDate = Parser.parseEventDate(command);
                Task task = new Event(eventDescription, eventDate);
                tasks.addTask(task);
                ui.showAddTask(tasks.list);
            } else if (commandType.equals("delete")) {
                int taskIndex = Parser.parseTaskIndex(command) - 1;
                Task deletedTask = tasks.list.get(taskIndex);
                tasks.removeTask(taskIndex);
                ui.showDeleteTask(tasks.list, deletedTask);
            } else if (commandType.equals("find")) {
                String keyword = Parser.parseKeyword(command);
                ArrayList<Task> matchingTasks = new ArrayList<>();
                for (Task task : tasks.list) {
                    if (task.description.contains(keyword)) {
                        matchingTasks.add(task);
                    }
                }
                ui.showMatchingTasks(matchingTasks);
            } else {
                throw new DukeException(ui.showInvalidCommand());
            }
        } catch (DukeException e) {
            System.out.println(e.getMessage());
        }
    }

}
