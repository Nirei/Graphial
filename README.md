Graphial
========
Using the twitter API, Graphial will, given an username, forge a DOT file with the interrelations of your friends wich you can graph using Dot, or similar software.

More specifically, Graphial will query for the followers of the account you introduce and then will check wich of those followers you follow.
It will then resolver their usernames and write down a file in dot format with each friend as a node and the relations among them as interconecting lines.

Build and run
=============
You can build Graphial by any means you find adequate. I use Eclipse and have included the .project file so you can do the same.

Run with

java -jar graphial.jar twitter_username output_file

After that, use dot or gvedit to graph the resulting file.

Given that the twitter API has an hourly rate limit of 150 calls, it may take about an hour or more to complete but subsequent uses of the program use cache files to save as many calls to the API as possible.
Since the caches are still very rudimentary, you will have to delete them manually if you want to update them.

License
=======
Meh, this is totally copyleft. Mess around with it as much as you desire, don't even bother to credit me for this shit.

Credit
======
This project includes a full unmodified copy of the one at https://github.com/douglascrockford/JSON-java
My respects to Mr. Crokford.