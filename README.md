# Advent of code utilities

This package contains some classes for Advent of code that are mainly used to generate files and download the input. It supports login using GitHub to specify the input target.

## Features

 - Automatic download of the personal input
 - Automatic generation of the file for the current day
 - Formatted javadoc based on the description for the task of the day
 - Automated launch of the file for the current day

## Setup

Simply reference this package in your project as explained on the 'Packages' tab. Alternatively, you can download the latest ``...-jar-with-dependencies.jar`` file and reference it directly.

By default, the generator will threat the top directory of the project as the default package. If you are using Maven or similar and the default package is in a subdirectory, you can specify that using ``Launcher.FILE_ROOT``. For example, for a maven project set it to ``"src/main/java/"``. Not the '/' at the end.

To recieve the personal input and to access the description of the second part of the task, a login is neccecary. Therefore, the program will ask you for username and password of your GitHub accout. The username if stored in ``recources/data/login.login``. The password will not be saved by default and you will have to enter it whenever neccecary. If you do want your passport to be saved, you can set ``Launcher.SAVE_PASSWORD`` to ``true``. However, **make sure to add the ``login.login`` file to your ``.gitignore``, or your password will be uploaded!**
