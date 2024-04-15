---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* The [original AB3 project](https://github.com/se-edu/addressbook-level3), which Nursing Address Book (NAB) is based from.
* Libraries used: [JavaFX](https://openjfx.io/), [JUnit5](https://github.com/junit-team/junit5), [Jackson](https://github.com/FasterXML/jackson)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<div align="center">
    <img src="images/ArchitectureDiagram.png" width="280" alt="Architecture diagram"/>
    <br>
</div>

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2324S2-CS2103T-F10-1/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2324S2-CS2103T-F10-1/tp/blob/master/src/main/java/seedu/address/MainApp.java) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<div align="center">
    <img src="images/ArchitectureSequenceDiagram.png" width="574" alt="Architecture sequence diagram" />
    <br>
</div>

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API 
`interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using 
the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component 
through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the 
implementation of a component), as illustrated in the (partial) class diagram below.

<div align="center">
    <img src="images/ComponentManagers.png" width="300" alt="Component managers"/>
    <br>
</div>

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2324S2-CS2103T-F10-1/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<div align="center">
    <img src="images/UiClassDiagram.png" alt="Ui class diagram"/>
    <br>
</div>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2324S2-CS2103T-F10-1/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2324S2-CS2103T-F10-1/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2324S2-CS2103T-F10-1/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<div align="center">
    <img src="images/LogicClassDiagram.png" width="550" alt="Logic class diagram"/>
    <br>
</div>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<div align="center">
    <img src="images/DeleteSequenceDiagram.png" alt="Delete sequence diagram"/>
    <br>
</div>

<div markdown="span" class="alert alert-info">
:information_source: <b>Note:</b> The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
2. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
3. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
4. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<div align="center">
    <img src="images/ParserClasses.png" width="600" alt="Parser classes"/>
    <br>
</div>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component

**API** : [`Model.java`](https://github.com/AY2324S2-CS2103T-F10-1/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<div align="center">
    <img src="images/ModelClassDiagram.png" width="700" alt="Model class diagram"/>
    <br>
</div>

The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

### Storage component

**API** : [`Storage.java`](https://github.com/AY2324S2-CS2103T-F10-1/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<div align="center">
    <img src="images/StorageClassDiagram.png" width="550" alt="Storage class diagram"/>
    <br>
</div>

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section outlines some notable details on how specific features are being implemented.

### Adding a patient into NAB

#### Implementation

The add patient feature is facilitated mainly by `AddCommand`, `AddCommandParser` and `LogicManager`.

Given below is an example usage scenario and how the add patient feature behaves at each step.

**Step 1.** The user inputs an add Command (e.g. `add n\Alice ic\A0055679T ad\01/01/2022 dob\01/01/2002 w\WA`) to add a new patient named Alice to the address book.  

<div markdown="span" class="alert alert-info">:information_source: **Note:** The format of the add command is as follows:  
  
- **n\\**: Indicates the name of the patient  
- **ic\\**: Indicates the NRIC of the patient  
- **ad\\**: Indicates the admission date of the patient into the hospital  
- **dob\\**: Indicates the date of birth of the patient  
- **w\\**: Indicates the ward the patient is currently in  
- **r\\**: Indicates remarks for the patient (optional, can only have 1)  
- **t\\**: Indicates the tags of the patient (optional, can have multiple)    
</div>


**Step 2.** `LogicManager#execute(String)` is called to execute the add command.   

**Step 3.** The command is parsed via `AddressBookParser#parseCommand(String)`, which calls `AddCommandParser#parse(String)` to parse the user input and creates a new `AddCommand` object.

**Step 4.** The created `AddCommand` is returned to `LogicManager`. Then, `AddCommand` is executed by calling `AddCommand#execute(Model)`.

**Step 5.** The `AddCommand#execute(Model)` then calls `Model#addPerson(Person)` to add the new patient to the address book.

**Step 6.** The `CommandResult` from the `AddCommand` object is returned to `LogicManager` and then to `UI` to display the success message.

**UML Diagrams:**

The following sequence diagram summarizes what happens when a user executes a new command:

<div align="center">
    <img src="images/AddSequenceDiagram.png" alt="Add sequence diagram"/>
    <br>
</div>

The following activity diagram summarizes what happens when a user executes a new command:

<div align="center">
    <img src="images/AddActivityDiagram.png" alt="Add activity diagram"/>
</div>

### Deleting a patient from NAB

#### Implementation

The delete patient feature is facilitated mainly by `DeleteCommand`, `DeleteCommandParser` and `LogicManager`.

Given below is an example usage scenario and how the delete patient feature behaves at each step.

**Step 1.** The user inputs a delete Command (e.g. `delete 1`) to delete the patient at index 1 in NAB.

**Step 2.** `LogicManager#execute(String)` is called to execute the delete command.

**Step 3.** The command is parsed via `AddressBookParser#parseCommand(String)`, which calls `DeleteCommandParser#parse(String)` to parse the user input and creates a new `DeleteCommand` object.

**Step 4.** The created `DeleteCommand` is returned to `LogicManager`. Then, `DeleteCommand` is executed by calling `DeleteCommand#execute(Model)`.

**Step 5.** The `DeleteCommand#execute(Model)` then calls `Model#deletePerson(Person)` to delete the patient from the address book.

**Step 6.** The `CommandResult` from the `DeleteCommand` object is returned to `LogicManager` and then to `UI` to display the success message.

**UML Diagrams:**

Given below is the sequence diagram that summarizes what happens when a user executes a new command:

<div align="center">
    <img src="images/DeleteSequenceDiagram.png" alt="Delete sequence diagram"/>
    <br>
</div>

The following activity diagram summarizes what happens when a user executes a new command:

<div align="center">
    <img src="images/DeleteActivityDiagram.png" alt="Delete activity diagram"/>
</div>

### Editing a patient's details

#### Implementation

Editing a patient's details is facilitated mainly by `EditCommand`, `EditCommandParser` and `LogicManager`.

Given below is an example usage scenario and how the edit patient feature behaves at each step.

**Step 1.** The user inputs an edit Command (e.g. `edit 1 w\WB`) to edit the ward of the patient at index 1 in NAB.

**Step 2.** `LogicManager#execute(String)` is called to execute the edit command.

**Step 3.** The command is parsed via `AddressBookParser#parseCommand(String)`, which calls `EditCommandParser#parse(String)`to parse the user input and creates a new `EditCommand` object.

**Step 4.** A new `EditPersonDescriptor` object is created with the new ward details. 
A new `EditCommand` instance will be created with the index of the patient to be edited and the new `EditPersonDescriptor` object.

**Step 5.** The `EditCommand` instance is returned to the `LogicManager` and `execute` is called.

**Step 6.** The `EditCommand` instance calls `Model#setPerson(Person, Person)` to edit the patient's details.
The patient specified will have its ward updated to the new ward specified.

**UML Diagrams:**

The following sequence diagram summarizes what happens when a user executes an edit command:

<div align="center">
    <img src="images/EditSequenceDiagram.png" alt="Edit sequence diagram"/>
    <br>
</div>

The following activity diagram summarizes what happens when a user executes an edit command:

<div align="center">
    <img src="images/EditCommandActivityDiagram.png" alt="Edit activity diagram"/>
</div>

### Showing help for commands

#### Implementation

Showing help for commands is facilitated by the `HelpCommand` class. It allows users to view the usage instructions for the application.

<div align="center">
    <img src="images/HelpClassDiagram.png" alt="Help class diagram"/>
    <br>
</div>

The `HelpCommand` class interacts with the following components:

* `Logic`: The `HelpCommand` class is part of the `Logic` component and is executed by the `LogicManager` when the user enters the `help` command.
* `UI`: The `MainWindow` class in the `UI` component subscribes to the `CommandResult` event raised by the `HelpCommand` and displays the help message in the `HelpWindow`.

The `HelpCommand` class extends the `Command` interface and implements the `execute()` method. When the `help` command is executed, the `HelpCommand#execute()` method is called, which creates a `CommandResult` object with the help message to be displayed to the user.

Here are the steps involved when a user calls the `help` command:

**Step 1.** The user enters the `help` command in the CLI.

**Step 2.** The `LogicManager` receives the command and calls the `parseCommand()` method of the `AddressBookParser`.

**Step 3.** The `AddressBookParser` identifies the command as a `HelpCommand` and creates a new instance of the `HelpCommand` class.

**Step 4.** The `AddressBookParser` returns the `HelpCommand` instance to the `LogicManager`.

**Step 5.** The `LogicManager` calls the `execute()` method of the `HelpCommand` instance.

**Step 6.** The `HelpCommand#execute()` method creates a new `CommandResult` object with the help message.

**Step 7.** The `CommandResult` object is returned to the `LogicManager`.

**Step 8.** The `LogicManager` passes the `CommandResult` to the `MainWindow` for display.

**Step 9.** The `MainWindow` extracts the help message from the `CommandResult` and displays it in the `HelpWindow`.

The following sequence diagram shows how the help command works:

<div align="center">
    <img src="images/HelpSequenceDiagram.png" alt="Help sequence diagram">
</div>

#### Design considerations

**Aspect: Displaying the help message**

* **Alternative 1 (current choice)**: Display the help message in a separate window.
    * Pros: Allows the user to view the help message without navigating away from the main window.
    * Cons: Requires additional implementation to create and manage a separate help window.

* **Alternative 2**: Display the help message in the main window's result display.
    * Pros: Simpler implementation, as it reuses the existing result display component.
    * Cons: The help message may be obscured by subsequent command results, and the user may need to scroll to view the entire message.

**Aspect: Providing command-specific help**

* **Alternative 1 (current choice)**: Provide a general help message that covers all commands.
    * Pros: Simplifies the implementation and maintains a consistent help message format.
    * Cons: The user may need to read through the entire help message to find information about a specific command.

* **Alternative 2**: Provide command-specific help messages.
    * Pros: Allows users to quickly access help information for a particular command.
    * Cons: Requires additional implementation to map each command to its specific help message, and may lead to inconsistencies in the help message format.

#### Future enhancements

Here are some possible enhancements for the help feature:

* Implement context-sensitive help that provides guidance based on the user's current input or state.
* Add support for displaying rich media content, such as images or videos, to provide more engaging and informative help content.
* Integrate a search functionality within the help system to allow users to quickly find relevant information.

These enhancements would improve the user experience and make the help feature more interactive and user-friendly.

### List by tags and/or ward feature

#### Implementation

The filter by tags and/or ward mechanism is facilitated by `ListCommand`, `ListCommandParser`, `ListKeywordsPredicate`
and `LogicManager`, which implements the `Logic` interface. `LogicManager` holds a `AddressBookParser` that parses the 
user input, and a model where the command is executed. Additionally, it implements the following operations:

* `LogicManager#execute(String)`— Executes the given user String input to return a `CommandResult` object.

These operations are exposed in the UI interface as `MainWindow#executeCommand(String)`.

Given below is an example usage scenario and how the filter by tags and/or ward mechanism behaves at each step.

**Step 1.** The user executes `list t\diabetes` command to list patients with the diabetes tag in the address book. 

- The `list` command triggers `AddressBookParser` to parse the user input, identifying the `list` command word to call
  `ListCommandParser#parse(String)` to parse the rest of the user input.

**Step 2.** `ListCommandParser#parse(String)` parses the user input. If there are subsequent arguments, a 
`ListKeywordPredicate` object is created with the keyword(s) provided. The keywords can be either a ward or tag(s) or both,
supplied by the user with the relevant prefix. In this case, it would be a tag, `t\diabetes`.

**Step 3.** A new `ListCommand` instance is created using the `ListKeywordsPredicate` object. (If there are no parameters 
provided, it will still simply create a `ListCommand` object.)  

The following is a list of objects created thus far:

<div align="center">
    <img src="images/ListObjectDiagram.png" width="280" alt="List object diagram"/>
    <br>
</div>

**Step 4.** The `ListCommand` object is returned to `LogicManager` and `execute` is called. `ListCommand#execute(Model)`
filters the list of patients in `Model` based on the `ListKeywordsPredicate` object if it is present. (Otherwise, it
returns the full list of patients.)

**Step 5.** The filtered list of patients (with diabetes) is displayed to the user through the GUI.

**UML Diagrams:**

The following sequence diagram shows how the listing of relevant patients would work:

<div align="center">
    <img src="images/ListCommandSequenceDiagram.png" alt="List sequence diagram"/>
    <br>
</div>

The following activity diagram summarizes what happens when a user executes a new command to list relevant patients:

<div align="center">
    <img src="images/ListCommandActivityDiagram2.png" width="500" alt="List activity diagram"/>
</div>

#### Design considerations

**Aspect: Command to filter:**

* **Alternative 1 (current choice):** Add the parameters to list command instead of find.
    * Pros: More appropriate as `list` will produce a useful list of relevant contacts, whereas `find` implies that only one useful result is expected.
    * Cons: May not have such a clear distinction between `list` and `find`, as user just wants to search for results.

* **Alternative 2:** Add parameters to find command instead of list.
    * Pros: Easy to use, one command will perform all search.
    * Cons: Unable to differentiate usefulness.

**Aspect: Filter by a ward or many wards:**

* **Alternative 1 (current choice):** Allow filtering only by at most 1 ward input.
    * Pros: More targeted; Nurses will only refer to the ward that they are in or are visiting. Otherwise, will filter across all wards.
    * Cons: Does not allow looking at specific, multiple wards.

* **Alternative 2:** Allow multiple ward tags.
    * Pros: Easy to be more specific.
    * Cons: Not relevant for the nurses use case. Allowing more wards also make it harder to filter fast.

### Find a patient by name or NRIC

#### Implementation

The find feature is facilitated by `FindCommand`, `FindCommandParser`, `NameContainsKeywordsPredicate` and
`IcContainsKeywordsPredicate`.

Given below is an example usage scenario and how the find by name or IC mechanism behaves at each step.

**Step 1.** The user executes `find n\Bob` command to find patient with the name Bob in the address book. The
`FindCommandParser` parses the user input, creating a new `FindCommand` and `NameContainsKeywordsPredicate` object.

**Step 2.** For each patient in the address book, the `predicate` object will be passed to
`Model#updateFilteredPersonList` check if the patient has Bob as part of his/her name. If the patient has the name Bob,
the patient will be shown in result.

**UML Diagrams:**

The following sequence diagram shows how the finding of patient would work:

<div align="center">
    <img src="images/FindSequenceDiagram.png" alt="Find sequence diagram"/>
    <br>
</div>

The following activity diagram summarizes what happens when a user executes a new command to find a patient:

<div align="center">
    <img src="images/FindActivityDiagram.png" alt="Find activity diagram"/>
</div>

#### Design considerations:

**Aspect: Find by full word or letters:**

* **Alternative 1 (current choice):** Allow finding by full word only.
    * Pros: More meaningful to find by words instead of just letters.
    * Cons: Harder to search when patient details is insufficient.

* **Alternative 2:** Allow finding by letters.
    * Pros: Able to search even if lacking patient information.
    * Cons: Harder to get specific patient as result will be a list.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------
## **Appendix**

### **Appendix: Requirements**

#### Product scope

**Target user profile**:
Ward nurses
* Manage a significant number of patient contacts with varying details
* Quickly access critical patient information in time-sensitive situations
* Track and log details of care administered to each patient over time

**Preferences/Skills**
* Prefer desktop apps over other types
* Can type fast
* Prefers typing to mouse interactions
* Is reasonably comfortable using CLI (command-line interface) apps

**Value proposition**: streamlined text-based commands to manage contacts faster than a typical mouse/GUI driven app

#### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`


| Priority | As a …  | I want to …                                  | So that I can…                                               |
|----------|---------|----------------------------------------------|--------------------------------------------------------------|
| `* * *`  | user    | add patient records                          | keep track of their medical condition in the hospital        |
| `* * *`  | user    | view existing patient records                | access information on existing patients                      |
| `* * *`  | user    | delete a patient record                      | remove outdated or irrelevant patient data                   |
| `* *`    | user    | edit patient records                         | ensure that all patient details are current and accurate     |
| `* *`    | user    | list patients based on their tags            | view patients based on category                              |
| `* *`    | user    | list all patients tied to a specific ward    | know which patients belong to which ward                     |
| `* *`    | user    | find patients via their name or NRIC         | quickly find information of specific patient                 |
| `* *`    | user    | access the user guide through the app easily | learn how to use the NAB                    |
| `*`      | user    | view patient list sorted by name             | look through the list of patients in a more organized manner |

#### Use cases

(For all use cases below, the **System** is the `NAB` and the **Actor** is the `user`, unless 
specified otherwise)

**Use case: `UC01 - View all patient records`**

**MSS**

1.  User requests to list patients.
2.  NAB shows a list of patients.

    Use case ends.

**Extensions**

* 1a. NAB detects that the command is invalid. 
  * 1a1. NAB shows an error message.

  Use case ends.

**Use case: `UC02 - Add a patient`**

**MSS**

1.  User requests to add a patient.
2.  NAB adds the patient.
3.  NAB shows success message to the user.

    Use case ends.

**Extensions**

* 1a. NAB detects that the patient details is invalid.
    * 1a1. NAB shows an error message.

    Use case ends.

**Use case: `UC03 - Delete a patient`**

**MSS**

1.  User requests to <u>view patient records(UC01)</u>.
2.  User requests to delete a patient in the list.
3.  NAB deletes the person.
4.  NAB shows success message to the user.

    Use case ends.

**Extensions**

* 2a. The given index is invalid.
  * 2a2. NAB shows an error message.

  Use case ends.

**Use case: `UC04 - Edit a patient's records`**

**MSS**

1.  User requests to <u>view patient records(UC01)</u>.
2.  User requests to edit a patient's record in the list.
3.  NAB edits the patient's record.
4.  NAB shows success message to the user.

    Use case ends.

**Extensions**

* 2a. NAB detects that the patient details is invalid.
    * 2a1. NAB shows an error message.

  Use case ends.

**Use case: `UC05 - Find patient by name`**

**MSS**

1. User requests to find a patient in the list with specific name. 
2. NAB shows the patient.

    Use case ends.

**Extensions**

* 1a. NAB detects that the given parameter is invalid.
    * 1a1. NAB shows an error message.
* 1b. No patient with specified name is present in the NAB.
    * 1b1. NAB displays an empty list.    

    Use case ends.

**Use case: `UC06 - Find patient by NRIC`**

**MSS**

1. User requests to find a patient in the list with specific NRIC.
2. NAB shows the patient.

   Use case ends.

**Extensions**

* 1a. NAB detects that the given parameter is invalid.
    * 1a1. NAB shows an error message.
* 1b. No patient with specified NRIC is present in the NAB.
    * 1b1. NAB displays an empty list.

  Use case ends.

**Use case: `UC07 - View patient with specific tags`**

**MSS**

1. User requests to view patients with specific tags.
2. NAB shows the patient list.

   Use case ends.

**Extensions**

* 1a. NAB detects that the given parameter is invalid.
    * 1a1. NAB shows an error message.
* 1b. No patient with the specified tags are found in NAB.
    * 1b1. NAB displays an empty list.

    Use case ends.

**Use case: `UC08 - View patients in specific ward`**

**MSS**

1. User requests to view patients in specific ward.
2. NAB shows the patient.

   Use case ends.

**Extensions**

* 1a. NAB detects that the given parameter is invalid.
    * 1a1. NAB shows an error message.
* 1b. No patient with specified ward is present in the NAB.
    * 1b1. NAB displays an empty list. 

    Use case ends.

**Use case: `UC09 - Get help with command usage`**

**MSS**

1. User requests to get help with command usage.
2. NAB shows command usage.

   Use case ends.

**Extensions**

* 1a. NAB detects that the command is invalid.
    * 1a1. NAB shows an error message.

    Use case ends.

#### Non-Functional Requirements

1.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands)
    should be able to accomplish most of the tasks faster using commands than using the mouse.
1.  The codebase should be well-structured and well-documented to facilitate future maintenance and enhancements.
1.  The application should only support a single user.
1.  The data should be stored locally and should be in a human editable text file.
1.  The software should work without requiring an installer.
1.  The software should not depend on any specific remote server.
1.  The product should be available as a single JAR file of size 100MB or below.
1.  The product should process a user input command within 2 seconds.

--------------------------------------------------------------------------------------------------------------------

### **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

[//]: # (<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;)

[//]: # (testers are expected to do more *exploratory* testing)

[//]: # (</div>)

#### Launch and shutdown

1. Initial launch

    1. Download the jar file and copy into an empty folder
    2. Double-click the jar file <br>
       Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.


2. Saving window preferences

    1. Resize the window to an optimum size. Move the window to a different location. Close the window.
    2. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

#### Adding a patient

1. Adding a patient

    1. Prerequisites: There exists no patient with NRIC `A1234567B` in the patient records.

    2. Test case (Valid parameters): `add n\John Smith ic\A1234567B dob\01/01/2000 ad\02/02/2020 w\a1 t\diarrhea
       r\likes to go to the park`<br>
       Expected: Patient successfully added into patient list. Details of the added patient shown in the status bar.

    3. Test case (Missing parameter): `add n\John Smith`<br>
       Expected: No patient is added. Error details shown in the status bar.

    4. Test case (Invalid Name): `add n\ ic\A1234567B dob\01/01/2000 ad\02/02/2020 w\a1 t\diarrhea r\likes to go to the park`<br>
       Expected: Similar to previous.

    5. Test case (Invalid NRIC): `add n\John Smith ic\A12347B dob\01/01/2000 ad\02/02/2020 w\a1 t\diarrhea r\likes to go to the park`<br>
       Expected: Similar to previous.

    6. Test case (Invalid Date of Birth): `add n\John Smith ic\A1234567B dob\2000 ad\02/02/2020 w\a1 t\diarrhea r\likes to go to the park`<br>
       Expected: Similar to previous.

    7. Test case (Repeated Parameter): `add n\John Smith ic\A1234567B ic\A1234567B dob\01/01/2000 ad\02/02/2020 w\a1
       t\diarrhea r\likes to go to the park`<br>
       Expected: Similar to previous.

    8. Test case (Date of Birth after Admission Date): `add n\John Smith ic\A1234567B dob\03/03/2000 ad\01/01/1999 w\a1`<br>
       Expected: Similar to previous.

#### Viewing patients

1. Viewing all patients

    1. Prerequisites: Multiple patients in the patient list.

    2. Test case: `list`<br>
       Expected: List of patients is shown.

   3. Test case: `list 181` or any command with extra characters supplied<br>
      Expected: No change in displayed patient list. Error details shown in the status message.


2. Viewing patients by tags and ward

    1. Prerequisites: Multiple patients in the patient list.

    1. Test case: `list tag\diarrhea w\a1`<br>
       Expected: List of patients is shown.

    1. Test case: `list t\diarrhea`<br>
       Expected: List of patients is shown.

    1. Test case: `list w\a1`<br>
       Expected: List of patients is shown.

#### Editing a patient

1. Edit a patient while all patients are being shown

    1. Prerequisites: List all patients using the `list` command. Multiple patients in the list.

    1. Test case: `edit 1 n\John`<br>
       Expected: Name of first patient is changed. Details of the edited patient is shown in the status bar.

    1. Test case: `edit 1 ic\W9876543M`<br>
       Expected: NRIC of first patient is changed. Details of the edited patient is shown in the status bar.

    1. Test case: `edit 1 dob\03/03/2005`<br>
       Expected: Date of birth of first patient is changed. Details of the edited patient is shown in the status bar.

    1. Test case: `edit 1 ad\05/05/2021`<br>
       Expected: Admission date of first patient is changed. Details of the edited patient is shown in the status bar.

    1. Test case: `edit 1 t\flu r\afraid of darkness`<br>
       Expected: tag and remark of first patient is changed. Details of the edited patient is shown in the status bar.

    2. Test case (Invalid Index): `edit x n\John` where x is larger than list size <br>
       Expected: Error details shown in status bar.

    1. Test case (Invalid Name): `edit 1 n\ `<br>
       Expected: Patient name is not changed. Error details shown in the status bar.

    2. Test case (Invalid NRIC): `edit 1 ic\a1231234b`<br>
       Expected: Patient NRIC is not changed. Error details shown in the status bar.

    3. Test case (Invalid Date of Birth): `edit 1 dob\03-03-2004`<br>
       Expected: Patient Date of Birth is not changed. Error details shown in the status bar.

    4. Test case (Invalid Admission Date): `edit 1 ad\04-02-2024 `<br>
       Expected: Patient Admission Date is not changed. Error details shown in the status bar.

    5. Test case (Date of Birth after Admission Date): `edit 1 dob\03/03/2024 ad\01/01/2024 `<br>
       Expected: Patient Date of Birth and Admission Date is not changed. Error details shown in the status bar.

    6. Test case (Invalid Ward): `edit 1 w\B-1 `<br>
       Expected: Patient ward is not changed. Error details shown in the status bar.

#### Finding a patient

1. Finding a patient by name

    1. Prerequisites: There exist only 1 patient `John Smith` in the patient records.

    1. Test case: `find n\John Smith`<br>
       Expected: `John Smith` is shown.

    1. Test case: `find n\john`<br>
       Expected: Similar to previous.

    1. Test case: `find n\Smith`<br>
       Expected: Similar to previous.

    1. Test case: `find n\j`<br>
       Expected: No patient is shown.

    1. Test case: `find n\`<br>
       Expected: Similar to previous.

2. Finding a patient by NRIC

    1. Prerequisites: There exist a patient with the NRIC `A1234567B` in the address book.

    1. Test case: `find ic\A1234567`<br>
       Expected: The patient with the NRIC `A1234567B` is shown.

    1. Test case: `find ic\a1234567b`<br>
       Expected: Similar to previous.

    1. Test case: `find ic\`<br>
       Expected: No patient is shown.

#### Deleting a person

1. Deleting a patient while all patients are being shown

    1. Prerequisites: List all patients using the `list` command. Multiple patients in the list.

    1. Test case: `delete 1`<br>
       Expected: First patient is deleted from the list. Details of the deleted contact shown in the status message.

    1. Test case: `delete 0`<br>
       Expected: No patient is deleted. Error details shown in the status message.

    1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to previous.


#### Saving data

1. Dealing with missing/corrupted data files

    1. Prerequisites: The addressbook.json file in the data directory must exist.

    1. Test case: Delete the addressbook.json file.<br>
    Expected: The app launches successfully, populated with the sample data.

--------------------------------------------------------------------------------------------------------------------

### Appendix: Planned Enhancements
Team size: 5

**1.  Checking if a date is valid:**
    Currently, the app performs some form of date validation in terms of the syntax of the date. For the day, month and
    year, the range is also limited to within reasonable calendar days (i.e. up till 31), months (i.e. up till 12) and
    years (i.e. up till 9999). The entire date itself must also be up till the current date. However, the app does not
    check if the date is valid. For example, a nurse could key in `30/02/2022` (reasonable range for each date segment 
    and before current date) as the date of birth of a patient. 

For more accurate error checking, we plan to perform more concrete validation of the dates to check if the date exists.
    This is done by checking if the input date is a valid date as a whole (not just its parts), and throwing the error 
    that the date does not exist if it is invalid.

**2. Standardise patient look-up/ accessing with the NRIC:**
    Currently, the app does not allow users to `edit` or `delete` a patient's details by their NRIC number. As patients 
    will predominantly (except foreigners) have a unique NRIC number, it would be more intuitive for nurses to be able 
    to `edit` and `delete` with an NRIC parameter as well, switching to `INDEX` should the patient not have an NRIC.

**3. Editing of patient details:**
   Currently, the app does not directly edit patient contact details from the patient list. Some patient entries (or
   details) may be accidentally deleted. For example, when editing the tags field, all tags will be overwritten. This
   may result in key information being lost. 

We plan to allow nurses to view the corresponding patient's details while editing. This can be done by having the nurse
    first input only `edit INDEX` (as per the original command). The app will then show the patient's details. 
    Subsequently, the nurse will be able to edit specific field(s) of the patient's details with a follow-up command, 
    while referencing the patient data. 

Alternatively, we plan to allow nurses to edit the fields directly, after specifying the patient to be edited.

Either approach will reduce accidental deletion of critical details.


--------------------------------------------------------------------------------------------------------------------


| Term                 | Further Explanation, Representation and Examples                                                                                                                          |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **CLI**              | Command Line Interface. It is a text-based interface where you input commands to interact with the system. e.g., the command terminal. Perfect for fast typists like you! |
| **Command**          | An instruction given to the application to perform a specific task. e.g., `add`, `list`, `delete`.                                                                        |
| **Command Terminal** | A text-based interface where you can input commands to interact with the computer's operating system. e.g., `cmd` for Windows users.                                      |
| **Data File**        | A file that stores the data of the application. e.g., `addressbook.json`.                                                                                                 |
| **GUI**              | Graphical User Interface. It is the visual representation of the system you see. e.g., Windows Desktop, Chrome Browser.                                                   |
| **JSON**             | JavaScript Object Notation. It is a data file type. For e.g., to store contacts saved in NAB.                                                                             |
| **Mainstream OS**    | Windows, Linux, Unix, MacOS. The most commonly used operating systems.                                                                                                    |
| **NRIC**             | Identity card number of the National Registration Identity Card. It is a unique identifier for individuals in Singapore. e.g., `S1234567A`.                               |
| **Parameter**        | A value that is passed to a command to perform a specific task. e.g., `n\John Doe`, `ic\T1234567P`.                                                                       |
| **Person/Patient**   | A contact entry in NAB, who is receiving medical services.                                                                                                                |
| **Prefix**           | A string of characters that precedes a parameter to indicate the type of parameter. e.g., `n\John Doe`, `ic\T1234567P`.                                                   |

--------------------------------------------------------------------------------------------------------------------
