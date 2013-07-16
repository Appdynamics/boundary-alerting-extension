# AppDynamics Boundary - Alerting Extension

##Use Case

While AppDynamics offers an inside-out visibility into your
applications, Boundary 
([http://www.boundary.com](http://www.boundary.com)) offers an
outside-in perspective. Boundary is a modern IT operations management application that operates at the
TCP/IP packet layer and provides a network-centric view of distributed applications.

With this integration extension, AppDynamics Pro events related to slow transactions or policy violations 
are relayed to Boundary and depicted as
annotations on the Boundary graphs. A Boundary user can click on the URL
in the annotation and it will launch the AppDynamics UI in context
so you can drilldown into that incident in AppDynamics.

##Installation 

1.  Download the appropriate tar.gz or zip file and extract it.
2.  For on-premise Controllers: Copy the contents of either boundary\_linux or boundary_windows, depending on the system type, to your 
controller installation directory on the machine where the AppDynamics Controller is running.
   
     For SaaS Controllers: contact AppDynamics Support so they can provision a single-tenant Controller for you and extract the boundaryClient file on your behalf.
2.   Edit the file \<controller-home\>/custom/conf/boundary.conf and
    change the properties to suit your environment:

    ```
#------------------------------------------------------------- 
# AppDynamics->Boundary notification client configuration file
#------------------------------------------------------------- 
# API Host for Boundary boundary_host=api.boundary.com 
# Organization id org_id=L3gI1Wuw6qDgikcRH01Vhe7GtY9 
# API key api_key=JQRBkA0G1Bss31i8foi14BiWi7W 
```

2. Use the AppDynamics Controller screen to configure the custom
    actions "notify-boundary-of-event" and
    "notify-boundary-of-policy-violation" in the Global Notifications
    and Policy Notifications screens. For details see the AppDynamics documentation website:
    [http://docs.appdynamics.com](http://docs.appdynamics.com) (login required)

###AppDynamics Alerts on Boundary Console

![](http://appsphere.appdynamics.com/t5/image/serverpage/image-id/55i85918054BA470BBA/image-size/original?v=mpbl-1&px=-1)

###Cross-launch to Incident screen on AppDynamics Console

![](http://appsphere.appdynamics.com/t5/image/serverpage/image-id/57iCD95B0C93001BE56/image-size/original?v=mpbl-1&px=-1)

##Contributing

Always feel free to fork and contribute any changes directly via GitHub.


##Support

For any support questions, please contact ace@appdynamics.com.
