# facebook integration and data analysis

The reason why i picked Facebook for this concept, Its the only social app for now which has entire user data apart from google . 
The data is big and helps to analyze any person's profile(of course if they provide permission)

This app will help to integrate facebook and analyze facebook's user data by reading user's entire history of posts and their interests,Tv shows,movies,places,books etc .The data read is streamed using kafka and write them into AWS S3 bucket.
Once user login into the app , user will see his pie chart of his interests and his favorites shows,books etc,Also this data will be streamed and that can be used for his recommendations later

Technologies used:
Front End: HTML,Javascript,jQuery,CSS,D3JS(Data Visulatization) 
Back End : java/J2ee,Spring MVC,restFB Postgress DB
Streaming : Zookeeper,Apache Kafka,
Sorage Service: AWS S3
WebServer:Tomcat

You'll need to have following things to run this app.
1) Create an app in https://developers.facebook.com/apps/ and provide the app id in index.html
2)You should have a proper AWS S3 bucket and accesskey and secert Key and provide them in dev.propeties file
3)All the required jars like kafka,restfb etc are committed along with source
4)You'll need Apache zookeeper and Kafka for streaming
5) D3js for Data visualization

