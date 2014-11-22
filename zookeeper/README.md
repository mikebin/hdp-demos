Zookeeper for Application Configuration
=======================================

This demo walks through a simple application which uses Zookeeper for dynamic configuration. One of the challenges in any application is how best to manage various configurable properties, externalizing them from the application code, and dealing with different environments (dev, test, prod, etc.). Zookeeper can be a useful tool for this purpose, since it provides a highly available, lightweight, centralized mechanism for storing and sharing small amounts of data across a distributed system.

The demo application is a simple Spring Boot/AngularJS RESTful web app which retrieves a few configuration properties from Zookeeper and displays them on a web page. We will preload some configuration settings in Zookeeper for two different "environments" - `dev` and `test`. The application uses the Apache Curator framework as a high level abstraction over the native Zookeeper APIs: http://curator.apache.org/.

A running Zookeeper Quorum is needed for this demo. The instructions below were tested with HDP 2.2.

Running the demo
----------------

- Build the web application
   
   From the base directory of the project, run the following to compile and package the web application:

  ```
  ./gradlew build
  ```
  
  This will build an executable jar `build/libs/zkconfig.jar`.

- Preload some configuration settings into Zookeeper

   Have a look at `populate.zk`. It creates a hierarchy of application configuration settings in Zookeeper, of the format: `/appconfig/<environment>/<appname>/db/...`

  Let's load these settings into Zookeeper using the ZK CLI.

  ```
  /usr/hdp/current/zookeeper-client/bin/zkCli.sh -server "zknode:2181" < populate.zk
  ```

  The `-server` option is not required if you're running the CLI on a ZK quorum node.

  If you've already loaded the sample configuration and want to reset to the initial state, you can first run the same command above with the `delete.zk` script.

- Start up the web application

  If needed, copy `build/libs/zkconfig.jar` to a node with access to the ZK quorum, the run the application as follows:

  ```
   java [-Dserver.port=<webport>] -Denv=[dev|test] -Dzk.connection.string="zknode1:2181[,zknode2:2181,...]" -jar zkconfig.jar
  ``` 

  For example:

  ```
  java -Dserver.port=9090 -Denv=dev -Dzk.connection.string="zknode1:2181,zknode2:2181,zknode3:2181" -jar zkconfig.jar
  ```

  The `server.port` setting is optional. By default, the application will try to run on port 8080.

- View the current configuration

  Open your browser to `http://host:port`, where `host` and `port` are the host and port number where your web application is running. You should see something like this:

  ![alt text](https://github.com/mikebin/hdp-demos/raw/master/zookeeper/images/zkconfig.png "ZK Config Web App")

- Make changes to your configuration
  
  Let's use the ZK CLI to make some changes to our configuration, and observe how the application receives these changes.

  ```
  /usr/hdp/current/zookeeper-client/bin/zkCli.sh -server "zknode:2181" 
  ```

  Now make a change to one of the config settings (modify the znode path as needed for your environment):

  ```
  [zk: localhost:2181(CONNECTED) 21] set /appconfig/test/myapp/db/user changed_user
  ```

  Now go back to your web application home page, and you should see the updated configuration setting appear (the page automatically refreshes its data every 5 seconds).

- Review the code

  The application code which retrieves configuration from Zookeeper is quite simple - have a look at how it works, and explore other APIs provided by Zookeeper and Curator. 
  
  As an extra challenge, implement watches for the configuration znodes, instead of polling for changes every time a REST request is received from the AngularJS client.
