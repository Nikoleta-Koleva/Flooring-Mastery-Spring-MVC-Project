Console application that reads and writes flooring orders for Wiley Edge Corp. The user can perform CRUD operations on data in txt files.

It utilizes Java, Spring MVC (Spring dependency Injection), OOP programming and stateful unit testing (JUnit 5 and Mockito).

Documentation:
There is a flowchart outlining the program flow, class diagram and a separate backup folder, to which the user can export their file data if they wish to.

The code is bullt based on MVC architecture:
- The model package may only contain classes that have data members (properties).
- The dao package contains classes that are responsible for persisting data.
- The controller package contains classes that orchestrate the program.
- The view package contains classes that interact with the user.
- The service package contains the service layer components.
- The UserIO class (along with the view component) will handle all console IO for the user.

To run the program, start the App class from an IDE such as IntelliJ IDEA, and choose an option from the console. 
