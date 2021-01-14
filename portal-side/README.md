![SocatelLogo](https://www.socatel.eu/wp-content/uploads/2018/02/logo_Socatel_L.png) 
# SoCaTel Platform
The SoCaTel platform is an open source co-creation platform adapted to the needs an preferences of key stakeholders 
involved in long-term care. This platform is the result of the project Horizon 2020 project Socatel.

For further information visit our [webpage](https://socatel.eu).

## Get started
This project has been developed using the [Spring Framework](https://spring.io/) in the backend, 
[Bootstrap](https://getbootstrap.com/) in the frontend, [Thymeleaf](https://www.thymeleaf.org/) as a tool 
to integrate the interaction between the frontend and the backend, [Maven](https://maven.apache.org/) as project 
management and comprehension tool and [MySQL](https://www.mysql.com/) for the database.

Therefore, the first steps would consist in installing the corresponding Java SDK, MySQL and Maven version. 
Once these are ready, there are some frontend requirements and backend dependencies, explained in the next chapter.

Finally, before executing, you have to fill the empty fields in the in the properties files (wrapped in {}) with
your own project properties.

### Requirements and Dependencies
#### Frontend requirements
- The frontend is mainly built using Boostrap 4.3.1 which is called in the \<head> section of the pages. 
You can keep this way to use Boostrap or download and modify it the \<head> section. 
- It uses [Fontawesome](https://fontawesome.com/start) Icons, you'll need to create your own kit and modify the 
following line in the 'head' section : \<script src="https://kit.fontawesome.com/16b12da617.js" 
th:src="@{https://kit.fontawesome.com/16b12da617.js}" crossorigin="anonymous"></script>.
- [JQuery](https://jquery.com/) is required and loaded in the \<head> section: 
    - [Emojionearea](https://github.com/mervick/emojionearea) ( Jquery Plugin ) is required to manage the emojis.
    - [Paginate.js](https://pagination.js.org/) ( Jquery Plugin for pagination ) is required for pagination.
- One component in the frontend (resource-registration twitter component) is coded in [Angular](https://angular.io/), 
so it requires installing the bower_components.
- [LESS](http://lesscss.org/) has been used to write CSS files. It is up to you to use them, modify them (recompiling)
or modify the headers to accept your custom CSS.

#### Project Dependencies
The project dependencies can be found at the pom.xml:
- [spring-boot-starter-security](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security)
- [spring-security-web](https://mvnrepository.com/artifact/org.springframework.security/spring-security-web)
- [spring-security-taglibs](https://mvnrepository.com/artifact/org.springframework.security/spring-security-taglibs)
- [spring-boot-starter-thymeleaf](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf)
- [thymeleaf-extras-springsecurity5](https://mvnrepository.com/artifact/org.thymeleaf.extras/thymeleaf-extras-springsecurity5)
- [spring-boot-starter-web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)
- [spring-boot-starter-actuator](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator)
- [mysql-connector-java](https://mvnrepository.com/artifact/mysql/mysql-connector-java)
- [spring-boot-starter-test](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test)
- [spring-security-test](https://mvnrepository.com/artifact/org.springframework.security/spring-security-test)
- [spring-boot-starter-data-jpa](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)
- [spring-boot-starter-mail](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-mail)
- [spring-boot-starter-json](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-json)
- [google-cloud-storage](https://mvnrepository.com/artifact/com.google.cloud/google-cloud-storage)
- [transport](https://mvnrepository.com/artifact/org.elasticsearch.client/transport)
- [elasticsearch-rest-high-level-client](https://mvnrepository.com/artifact/org.elasticsearch.client/elasticsearch-rest-high-level-client)
- [itextpdf](https://mvnrepository.com/artifact/com.itextpdf/itextpdf)
- [pdfbox](https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox)
- [spring-cloud-dependencies](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-dependencies)
- [spring-cloud-gcp-starter](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-gcp-starter)
- [spring-cloud-gcp-starter-sql-mysql](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-gcp-starter-sql-mysql)

#### External Dependencies / Features
This project, the Portal Side of the SoCaTel platform, is connected to an Elasticsearch instance that acts as a Raw 
Data Repository for sending data to the SoCaTel's Knowledge Base from the Portal Side and also retrieves data 
through Rest APIs from the Knowledge Base. In this chapter it is explained how to adapt this features to your own ones.

##### Elasticsearch
The configuration file is configurations/ElasticsearchConfig.java and it can be edited to fit your needs. The properties
needed can be found in **application.properties**; in order to work with your Elasticsearch instance, replace your own 
properties in:  
- spring.elasticsearch.rest.uris={your_uri(protocol://host:port)}
- spring.elasticsearch.rest.username={your_user}
- spring.elasticsearch.rest.password={your_password}
- spring.elasticsearch.rest.host={your_host}
- spring.elasticsearch.rest.port={your_port}

This feature works as following. Every time data is created or updated (mainly in the save method in the Services 
classes) or deleted (should be in the delete method in the Services classes, however in this project there 
are not deletes), the publisher publishers/ElasticsearchPublisher.java is called, invoking asynchronously an event 
events/ElasticsearchEvent with the corresponding data, and finally is handled in the listeners/ElasticsearchListener 
where the data is sent to the Elasticsearch.

The user data is a special case because the user_id is sent encrypted for security reasons. In order to keep this 
encryption, you have to replace this variables in the utils/Constants.java file:  

    private static final String SALT = "{your_salt}";  
    private static final String PASSWORD = "{your_password}";  
    public static final String NOISE_BEFORE = "{your_noise_before}";
    public static final String NOISE_AFTER = "{your_noise_after}";

The data sent to the Elasticsearch are not the database models themselves but the DTOs that can be found in the
knowledge_base_dump folder, with the help of the KBDump.java component.

##### REST APIs
Whereas the Elasticsearch is used by this project to send data to the Knowledge Base, various REST API calls are used
to retrieve data from the Knowledge Base. The configuration can be found in the utils/Constants.java, where we use 
2 different hosts with the same user and password:

    public static final String REST_API_USER = "{your_user}";
    public static final String REST_API_PASSWORD = "{your_password}";
    private static final String REST_API_HOST = "{your_host}";
    private static final String REST_API_HOST_REC = "{your_host_2}";

All the classes related to the REST APIs can be found in the rest_api folder, which each subfolder contains the 
DTOs used, and the RestAPICaller which has de http client configuration and perform the calls and retrieve the data. 
In addition, some unit tests have been done in the test folder for all the calls.

### Execution
The database can be executed at localhost, with docker images and connecting to an external database.

Regardless the execution type, you will have to create a database with a user with rights, and then run the 
scripts *schema.sql* and then the *inserts.sql* in the database folder in order to have the database ready.

#### Localhost
In this chapter it is explained how to execute the platform and the database in your localhost.

- In **application.properties**, replace your properties in: 
    - **spring.datasource.url**=jdbc:mysql://**localhost**:3306/{*your_database*}
    - **spring.datasource.username**={*your_username*}
    - **spring.datasource.password**={*your_password*}

- Make sure that:
    - In GCPStorage.java (in utils/ folder) the localBucketConfiguration() is uncomment and the 
productionBucketConfiguration() commented.
    - In ElasticsearchListener.java (in listeners/ folder) the @Component is commented.
    - In Constants.java all wrapped variables in '{}' are replaced.

And run *mvn spring-boot:run* in the root folder. Now you can access to the platform at localhost:8080.

#### Docker
In this chapter it is explained how to execute the platform and the database in docker images.

- In **application.properties**, replace your properties in: 
    - **spring.datasource.url**=jdbc:mysql://**mysql-socatel**:3306/{*your_database*}
    - **spring.datasource.username**={*your_username*}
    - **spring.datasource.password**={*your_password*}

- Make sure that:
    - In GCPStorage.java (in utils/ folder) the localBucketConfiguration() is uncomment and the 
productionBucketConfiguration() commented.
    - In ElasticsearchListener.java (in listeners/ folder) the @Component is commented.
    - In Constants.java all wrapped variables in '{}' are replaced.

- Edit the script **docker.sh** in the root folder, and modify the function *run_mysql* with your database properties.

- Run the script *./docker.sh start* in the  root folder. Now you can access to the platform at localhost:8080.

#### External database
In this chapter it is explained how to execute the platform connected to an external database.

- In **application.properties**, replace your properties in: 
    - **spring.datasource.url**=jdbc:mysql://**{external_ip}**/{*your_database*}
    - **spring.datasource.username**={*your_username*}
    - **spring.datasource.password**={*your_password*}

- Make sure that:
    - In GCPStorage.java (in utils/ folder) the localBucketConfiguration() is uncomment and the 
productionBucketConfiguration() commented.
    - In ElasticsearchListener.java (in listeners/ folder) the @Component is commented.
    - In Constants.java all wrapped variables in '{}' are replaced.
    
And run *mvn spring-boot:run* in the root folder. Now you can access to the platform at localhost:8080.

### Deployment
This project has been deployed in Google Cloud Platform, the database in the Cloud SQL service
and the platform in the App Engine Flexible service.

It has been done following [this tutorial](https://www.baeldung.com/spring-boot-google-app-engine).

The basic steps are:

- Create a Cloud SQL instance and run the database scripts.

- Create a maven profile 'cloud-gcp'.

- In **spring-cloud-bootstrap.properties**, replace your app name in **spring.cloud.appId**.

- In **app.yaml**, put your own configuration.

- In **application.properties**, replace your properties in: 
    - **spring.datasource.username**={*your_username*}
    - **spring.datasource.password**={*your_password*}

- In **application-gcp.properties**, replace your properties in: 
    - **spring.cloud.gcp.sql.instance-connection-name**={project-id}:{project-zone}:{db_instance}
    - **spring.cloud.gcp.sql.database-name**={database_name}
    - **spring.cloud.gcp.project-id**={project-id}

- Make sure that:
    - In application.properties (in root folder) the spring.datasource.url is commented.
    - In GCPStorage.java (in utils/ folder) the localBucketConfiguration() is comment and the 
productionBucketConfiguration() uncommented.
    - In ElasticsearchListener.java (in listeners/ folder) the @Component is uncommented.
    - In Constants.java all wrapped variables in '{}' are replaced.

Run *mvn clean package appengine:deploy -P cloud-gcp -Dmaven.test.skip* in the root folder.
The platform should be deployed in 10 minutes or so.

## License
Look at LICENSE.

## Project structure
Below it is shown the disposition of the different folders and relevant files.   

/                                ( *Root folder* )   
├─ database/                     ( *Database files* )   
│  └─ model/                     ( *Database model files - [see MySQL Workbench](https://www.mysql.com/products/workbench/)* )  
│  
├─ src/                          ( *SOCATEL PORTAL SIDE* )  
│  ├─ main/                      ( *Main files* )   
│  │  ├─ appengine/              ( *App Engine yaml file* )   
│  │  ├─ docker/                 ( *Dockerfile, needed in App Engine too* )  
│  │  ├─ java/com/socatel/       ( *BACKEND* )   
│  │  │  ├─ configurations/      ( *Configuration files* )   
│  │  │  ├─ controllers/         ( *Controllers* )   
│  │  │  ├─ controllers_rest/    ( *Rest controllers* )   
│  │  │  ├─ dtos/                ( *DTOs* )   
│  │  │  ├─ endpoints/           ( *Endpoints* )   
│  │  │  ├─ events/              ( *Events* )   
│  │  │  ├─ exceptions/          ( *Custom Exceptions* )   
│  │  │  ├─ filters/             ( *Request filters* )   
│  │  │  ├─ handlers/            ( *Handlers* )   
│  │  │  ├─ knowledge_base_dump/ ( *Raw Data repository DTOs* )   
│  │  │  ├─ listeners/           ( *Listeners* )   
│  │  │  ├─ models/              ( *Database models* )   
│  │  │  ├─ publishers/          ( *Publishers* )   
│  │  │  ├─ repositories/        ( *Repositories - [see JPA Repositories](https://docs.spring.io/spring-data/jpa/docs/1.6.0.RELEASE/reference/html/jpa.repositories.html)* )   
│  │  │  ├─ rest_api/            ( *Rest API calls from the Knowledge Base* )   
│  │  │  ├─ services/            ( *Services - [see Service](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/stereotype/Service.html)* )   
│  │  │  └─ utils/               ( *Useful files and enums* )     
│  │  │  
│  │  └─resources/               ( *FRONTEND* )  
│  │     ├─ static/     
│  │     │  ├─ assets/     
│  │     │  │  ├─ css/ 	         ( *CSS compiled files* )   
│  │     │  │  ├─ JS/ 	         ( *JS files* )  
│  │     │  │  └─ less/ 	     ( *LESS files, CSS sources* )  
│  │     │  │  
│  │     │  └─ Images/ 	         ( *Images* )  
│  │     │  
│  │     ├─ templates/ 	         ( *HTML files. The Fragments - [see Thymeleaf fragments](https://www.thymeleaf.org/doc/articles/layouts.html) - are named '-fragment.html'* )    
│  │     ├─ application          ( *Application properties* )  
│  │     ├─ application-gcp      ( *Google Cloud properties* )  
│  │     └─ messages*            ( *Translation files - [see i18n](https://www.baeldung.com/spring-boot-internationalization)* )  
│  │   
│  └─ test/java/com/socatel/     ( *Test files* )  
│  
├─ docker.sh                     ( *Docker script* )  
├─ pom.xml                       ( *Spring Framework project dependencies* )  
├─ LICENSE.txt                   ( *License file* )  
└─ README.md                     ( *This file* )  

## About us
Backend developer: [Víctor Colomé Carcolé](mailto:victorcolomec@gmail.com)\
Frontend developer: [François-Xavier Gourvès](mailto:fgourves@ozwillo.org)
- [Web page](https://www.socatel.eu/)
- [CORDIS](https://cordis.europa.eu/project/id/769975)
- [Twitter](https://twitter.com/socatel_co)
- [Facebook](https://www.facebook.com/socatel.cocreation/)
- [Linkedin](https://www.linkedin.com/in/socatel-co-creation-766693165/)
- [Youtube](https://www.youtube.com/channel/UC6k4Rf-W033c3varIzT6a9A/videos)
