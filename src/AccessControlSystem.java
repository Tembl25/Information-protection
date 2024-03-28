import java.io.*;
import java.util.Scanner;

public class AccessControlSystem {
    private static final int NUM_SUBJECTS = 9;
    private static final int NUM_OBJECTS = 4;

    private static final int[][] accessMatrix = new int[NUM_SUBJECTS][NUM_OBJECTS];

    private static final String[] userIDs = {"Ivan", "Sergey", "Boris", "Olga", "Dmitry", "Anna", "Alexey", "Vladimir", "Viktor"};

    public static void main(String[] args) {
        initializeAccessMatrix();
        printAccessMatrix();
        login();
    }

    private static void initializeAccessMatrix() {
        // Инициализация с полными правами у последнего пользователя
        for (int i = 0; i < NUM_SUBJECTS - 1; i++) {
            for (int j = 0; j < NUM_OBJECTS; j++) {
                accessMatrix[i][j] = (int) (Math.random() * 8);
            }
        }
        for (int j = 0; j < NUM_OBJECTS; j++) {
            accessMatrix[NUM_SUBJECTS - 1][j] = 7; // Полные права у последнего пользователя
        }
    }

    private static void printAccessMatrix() {
        System.out.println("Матрица прав:");
        for (int i = 0; i < NUM_SUBJECTS; i++) {
            System.out.print(userIDs[i] + ": ");
            for (int j = 0; j < NUM_OBJECTS; j++) {
                System.out.print(accessMatrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void login() {
        Scanner scanner = new Scanner(System.in);
        int userIndex;
        do {
            System.out.print("Введите идентификатор пользователя: ");
            String userID = scanner.nextLine();
            userIndex = -1;
            for (int i = 0; i < userIDs.length; i++) {
                if (userIDs[i].equals(userID)) {
                    userIndex = i;
                    break;
                }
            }
            if (userIndex == -1) {
                System.out.println("Пользователь не найден.");
            }
        } while (userIndex == -1);

        System.out.println("Идентификация прошла успешно. Добро пожаловать, " + userIDs[userIndex] + "!");
        displayAccessRights(userIndex);
        processUserCommands(userIndex, scanner);
    }


    private static void displayAccessRights(int userIndex) {
        System.out.println("Перечень прав пользователя " + userIDs[userIndex] + ":");
        for (int i = 0; i < NUM_OBJECTS; i++) {
            System.out.print("Объект" + (i + 1) + ": ");
            switch (accessMatrix[userIndex][i]) {
                case 0:
                    System.out.println("Полный запрет");
                    break;
                case 1:
                    System.out.println("Передача прав");
                    break;
                case 2:
                    System.out.println("Запись");
                    break;
                case 3:
                    System.out.println("Запись, Передача прав");
                    break;
                case 4:
                    System.out.println("Чтение");
                    break;
                case 5:
                    System.out.println("Чтение, Передача прав");
                    break;
                case 6:
                    System.out.println("Чтение, Запись");
                    break;
                case 7:
                    System.out.println("Полные права");
                    break;
            }
        }
    }

    private static void processUserCommands(int userIndex, Scanner scanner) {
        while (true) {
            System.out.print("Жду ваших указаний > ");
            String command = scanner.nextLine();
            if (command.equals("quit")) {
                System.out.println("Работа пользователя " + userIDs[userIndex] + " завершена. До свидания.");
                login(); // Запрос на вход нового пользователя
                break;
            } else if (command.equals("read") || command.equals("write") || command.equals("grant")) {
                System.out.print("Над каким объектом производится операция? ");
                int objectIndex = scanner.nextInt() - 1;
                scanner.nextLine(); // Чтение перевода строки после числа
                if (objectIndex < 0 || objectIndex >= NUM_OBJECTS) {
                    System.out.println("Некорректный номер объекта.");
                    continue;
                }
                switch (command) {
                    case "read":
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader("Object" + (objectIndex + 1) + ".txt"));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                            }
                            reader.close();
                        } catch (IOException e) {
                            System.out.println("Ошибка чтения: " + e.getMessage());
                        }
                        break;
                    case "write":
                        if (accessMatrix[userIndex][objectIndex] == 2 || accessMatrix[userIndex][objectIndex] == 3 ||
                                accessMatrix[userIndex][objectIndex] == 6 || accessMatrix[userIndex][objectIndex] == 7) {
                            System.out.print("Введите текст для записи: ");
                            String text = scanner.nextLine();
                            try {
                                BufferedWriter writer = new BufferedWriter(new FileWriter("Object" + (objectIndex + 1) + ".txt", true));
                                writer.write(text);
                                writer.newLine();
                                writer.close();
                                System.out.println("Текст сохранен успешно!");
                            } catch (IOException e) {
                                System.out.println("Ошибка при записи в файл: " + e.getMessage());
                            }
                            System.out.println("Операция прошла успешно");
                            break;
                        } else {
                            System.out.println("Отказ в выполнении операции. У Вас нет прав для ее осуществления");
                        }
                        break;
                    case "grant":
                        if (accessMatrix[userIndex][objectIndex] == 0 || accessMatrix[userIndex][objectIndex] == 2 || accessMatrix[userIndex][objectIndex] == 4 || accessMatrix[userIndex][objectIndex] == 6) {
                            System.out.println("Вы не имеете прав на передачу прав для данного объекта.");
                            break;
                        }
                        System.out.print("Право на какой объект передается? ");
                        int targetObjectIndex = scanner.nextInt() - 1;
                        scanner.nextLine(); // Чтение перевода строки после числа
                        if (targetObjectIndex < 0 || targetObjectIndex >= NUM_OBJECTS || targetObjectIndex != objectIndex) {
                            System.out.println("Некорректный номер объекта.");
                            continue;
                        }
                        System.out.print("Какое право передается? (read/write) ");
                        String accessType = scanner.nextLine();
                        System.out.print("Какому пользователю передается право? ");
                        int targetUserIndex = -1;
                        String targetUserID = scanner.nextLine();
                        for (int i = 0; i < userIDs.length; i++) {
                            if (userIDs[i].equals(targetUserID)) {
                                targetUserIndex = i;
                                break;
                            }
                        }
                        if (targetUserIndex == -1) {
                            System.out.println("Пользователь не найден.");
                            continue;
                        }
                        if (accessType.equals("read")) {
                            accessMatrix[targetUserIndex][targetObjectIndex] += 4;
                        } else if (accessType.equals("write")) {
                            accessMatrix[targetUserIndex][targetObjectIndex] += 2;
                        }
                        System.out.println("Операция прошла успешно");
                        break;
                }
            } else {
                System.out.println("Некорректная команда.");
            }
        }
    }
}