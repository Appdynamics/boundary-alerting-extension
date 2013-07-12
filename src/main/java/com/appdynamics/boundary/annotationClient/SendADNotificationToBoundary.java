/**
 * Copyright 2013 AppDynamics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.boundary.annotationClient;
/**
 *  This class will be used to relay AppDynamics notifications to the 
 *  Boundary Server via REST.
 *
 * Copyright (c) AppDynamics, Inc.
 * @author Pranta Das
 * Created on: September 18, 2012.
 */

import com.appdynamics.common.NotificationParameters;
import com.appdynamics.common.RESTClient;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

// Boundary imports

public class SendADNotificationToBoundary implements NotificationParameters 
{

    static {
         MDC.put("PID", ManagementFactory.getRuntimeMXBean().getName());
    }
	private static Logger logger =
				Logger.getLogger(SendADNotificationToBoundary.class);

    private static final String BANNER = "***************************"+
                "*****************************************************";

    private static final DateFormat df =
           new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");

	/**
	 * Parameter data types
	 */
	 static final String STRING = "STRING";
	 static final String INT = "INT";
	 static final String LONG = "LONG";
	 static final String STRING_ARRAY = "STRING_ARRAY";
	 static final String INT_ARRAY = "INT_ARRAY";

	 static final String[][] pvnParmsAndTypes = 
	{		
	 /*[0]*/{APPLICATION_NAME, STRING},
	 /*[1]*/{APPLICATION_ID, LONG},
	 /*[2]*/{PVN_ALERT_TIME, LONG},
	 /*[3]*/{PRIORITY, INT},
	 /*[4]*/{SEVERITY, STRING},
	 /*[5]*/{TAG, STRING},
	 /*[6]*/{PVN_RULE_NAME, STRING},
	 /*[7]*/{PVN_RULE_ID, LONG},
	 /*[8]*/{PVN_TIME_PERIOD_IN_MINUTES, INT},
	 /*[9]*/{PVN_AFFECTED_ENTITY_TYPE, STRING},
	 /*[10]*/{PVN_AFFECTED_ENTITY_NAME, STRING},
	 /*[11]*/{PVN_AFFECTED_ENTITY_ID, LONG},
	 /*[12]*/{PVN_NUMBER_OF_EVALUATION_ENTITIES, INT},
	 /*[13]*/{PVN_EVALUATION_ENTITY_TYPE, STRING},
	 /*[14]*/{PVN_EVALUATION_ENTITY_NAME, STRING},
	 /*[15]*/{PVN_EVALUATION_ENTITY_ID, LONG},
	 /*[16]*/{NUMBER_OF_TRIGGERED_CONDITIONS_PER_EVALUATION_ENTITY, INT},
	 /*[17]*/{PVN_TC_SCOPE_TYPE, STRING},
	 /*[18]*/{PVN_TC_SCOPE_NAME, STRING},
	 /*[19]*/{PVN_TC_SCOPE_ID, LONG},
	 /*[20]*/{PVN_TC_CONDITION_NAME, STRING},
	 /*[21]*/{PVN_TC_CONDITION_ID, STRING},
	 /*[22]*/{PVN_TC_OPERATOR, STRING},
	 /*[23]*/{PVN_TC_CONDITION_UNIT_TYPE, STRING},
	 /*[24]*/{PVN_TC_USE_DEFAULT_BASELINE, STRING},
	 /*[25]*/{PVN_TC_BASELINE_NAME, STRING},
	 /*[26]*/{PVN_TC_BASELINE_ID, LONG},
	 /*[37]*/{PVN_TC_THRESHOLD_VALUE, LONG},
	 /*[28]*/{PVN_TC_OBSERVED_VALUE, LONG},
	 /*[29]*/{PVN_SUMMARY_MESSAGE, STRING},
	 /*[30]*/{PVN_INCIDENT_ID, STRING},
	 /*[31]*/{CONTROLLER_DEEP_LINK_URL, STRING},
     /*[32]*/{PVN_EVENT_TYPE,STRING}
	};
	
	 static final int PVN_NUM_OF_EVAL_ENTITIES_INDEX = 12;
	 static final int PVN_NUM_OF_EVAL_ENTITIES_ATTRS = 3;
	 static final int PVN_NUM_OF_TRIG_CONDS_INDEX = 16;
	 static final int PVN_NUM_OF_TRIG_CONDS_ATTRS = 12;
	 static final int PVN_TC_CONDITION_UNIT_TYPE_INDEX = 23;
	 static final int PVN_NUMBER_OF_BASELINE_PARMS = 3;
	 static final String[][] eventNotificationParmsAndTypes =
	{		
	 /*[0]*/{APPLICATION_NAME, STRING},
	 /*[1]*/{APPLICATION_ID, LONG},
	 /*[2]*/{EN_TIME, INT},
	 /*[3]*/{PRIORITY, INT},
	 /*[4]*/{SEVERITY, STRING},
	 /*[5]*/{TAG, STRING},
	 /*[6]*/{EN_NAME, STRING},
	 /*[7]*/{EN_ID, LONG},
	 /*[8]*/{EN_INTERVAL_IN_MINUTES, INT},
	 /*[9]*/{EN_NUMBER_OF_EVENT_TYPES, INT, null},
	/*[10]*/{EN_EVENT_TYPE, STRING_ARRAY},
	/*[11]*/{EN_NUMBER_OF_EVENTS, INT_ARRAY},
	/*[12]*/{EN_NUMBER_OF_EVENT_SUMMARIES, INT},
	/*[13]*/{EN_EVENT_SUMMARY_ID, LONG},
	/*[14]*/{EN_EVENT_SUMMARY_TIME, STRING},
	/*[15]*/{EN_EVENT_SUMMARY_TYPE, STRING},
	/*[16]*/{EN_EVENT_SEVERITY, STRING},
	/*[17]*/{EN_EVENT_SUMMARY_STRING, STRING},
	/*[18]*/{CONTROLLER_DEEP_LINK_URL, STRING}
	};

	 static final int EN_NUM_OF_EV_TYPS_INDEX = 9;
	 static final int EN_NUM_OF_EV_TYPS_ATTRS = 2;
	 static final int EN_NUM_OF_EV_SUMRY_INDEX = 12;
	 static final int EN_NUM_OF_EV_SUMRY_ATTRS = 5;


	 private static final String TRUE = "true";

    private static final String APPD_BIZ_IMPACT_DETECTED = "AppDynamics:Business Impact Detected";

    private static final String
        boundaryClientHome = System.getProperty("BOUNDARY_CLIENT_HOME"),
        fileName = boundaryClientHome+"/conf/boundary.conf",
        BOUNDARY_HOST = "boundary_host",
        ORG_ID        = "org_id",
        API_KEY       = "api_key";

    public static String boundaryURL;
    public static String apiKey = "JQRBkA0G1Bss31i8foi14BiWi7W:";
	
	static String               hostname;
    static String               orgId;
	static Properties 		    theProperties;
	static String 				deepLinkUrl;


	/**
	 * Set up connection to the web server using the global configuration
	 * information from the input properties file
	 *  
	 * @return true if successful, false otherwise
	 */
	private static boolean setupConnection()
	{
        /**
         * Read the Boundary client properties file for the connection properties
         */
        theProperties = new Properties();

        System.getProperties();

        logger.info(" Using input properties file:" +
                fileName);

        FileInputStream propFile = null;
        try
        {
            propFile = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e)
        {
            logger.error(e);
            return false;
        }
        try
        {
            theProperties.load(propFile);
        }
        catch (IOException e)
        {
            logger.error(e);
            return false;
        }
        String strVal;

        if ((strVal = theProperties.getProperty(BOUNDARY_HOST)) != null)
            hostname = strVal.trim();

        if ((strVal = theProperties.getProperty(ORG_ID)) != null)
            orgId = strVal.trim();

        boundaryURL = "https://"+hostname+"/"+orgId+"/annotations";


        if ((strVal = theProperties.getProperty(API_KEY))
                                    != null)
            apiKey =  strVal.trim()+":";

		return true;
	}
	/**
	 * Tear down connection to the Boundary server
	 */
	private static void teardownConnection()
	{
	}


	/**
	 * Utility function to set a name value pair
	 * 
	 * @param nameValuePairs - The map comtaining the name value pairs
	 * @param name - the name of the attribute to set
	 * @param valueObject - the value of the attribute to set
	 * @param dataType - the data type of the attribute
     */
	private static void setNameValuePair(
					 HashMap<String, String> nameValuePairs,
					 String name,
					 String valueObject,
					 String dataType)
	{

		valueObject = transformIfNecessary(name, valueObject);

		nameValuePairs.put(name, valueObject);
	}


	/**
	 *	Transforms the value of an attribute if necessary. 
	 *
	 * @param name
	 * @param valueString
	 * @return
     */
	private static String transformIfNecessary(String name, String valueString)
	{
		if (name.equalsIgnoreCase(CONTROLLER_DEEP_LINK_URL))
		{

			deepLinkUrl = valueString;
		}

		return valueString;
	}

	private static void completeDeepLinkUrl(
					HashMap<String, String> nameValues)
	{
		long incidentOrEventId = 0L;

        if (nameValues.get(EN_EVENT_SUMMARY_ID) != null)
        {
                incidentOrEventId=
                        Long.parseLong(nameValues.get(EN_EVENT_SUMMARY_ID));
        }
        else
        if (nameValues.get(PVN_INCIDENT_ID) != null)
        {
            incidentOrEventId=
                    Long.parseLong(nameValues.get(PVN_INCIDENT_ID));
        }

        if (nameValues.get(CONTROLLER_DEEP_LINK_URL) != null)
        {
                nameValues.put(CONTROLLER_DEEP_LINK_URL, deepLinkUrl+incidentOrEventId);
                return;
        }
    }

	/**
	 * This method will send an event notification received from AppDynamics 
	 * to Boundary
	 * 
	 * @param args - the list of arguments received with the event notification
	 * @return true if all goes well, false otherwise
     */
	private static boolean sendEventNotification(String args[])
	{
		try
		{

			int numEventTypes=0;
			
			try 
			{
				numEventTypes = Integer.parseInt(
									args[EN_NUM_OF_EV_TYPS_INDEX+1]);
			}
			catch(NumberFormatException nfe)
			{
				logger.error("Unable to parse numEventTypes from:"+args[EN_NUM_OF_EV_TYPS_INDEX+1], nfe);
			}
			
			int numEventSummariesIndex = EN_NUM_OF_EV_TYPS_INDEX+2
									+(numEventTypes*EN_NUM_OF_EV_TYPS_ATTRS);
									

            int numEventSummaries = 0;
            try
            {
		    	numEventSummaries = Integer.parseInt(args[numEventSummariesIndex]);
            }
            catch(NumberFormatException nfe)
            {
                logger.error("Unable to parse numEventSummaries from:"+args[numEventSummariesIndex], nfe);
            }

			int numParms = EN_NUM_OF_EV_TYPS_INDEX+2
					 +(numEventTypes*EN_NUM_OF_EV_TYPS_ATTRS)
					 +(numEventSummaries*EN_NUM_OF_EV_SUMRY_ATTRS)+1,
			argsIndex=1,
			parmIndex=0;
			HashMap<String, String>[] nameValues =
									new HashMap[numEventSummaries];
			nameValues[0]= new HashMap<String,String>();

			for (int i=0; i < numParms; i++)
			{
				if ((argsIndex-1) == EN_NUM_OF_EV_TYPS_INDEX)
				{
					argsIndex++;
					StringBuffer eventTypes = new StringBuffer("{");
					StringBuffer numberOfEventsForEventType = new StringBuffer("{");
					for (int j = 0; j < numEventTypes; 
								j++, i+=EN_NUM_OF_EV_TYPS_ATTRS)
					{
                        if (j > 0)
                        {
                            eventTypes.append(", ");
                            numberOfEventsForEventType.append(", ");
                        }
						eventTypes.append(args[argsIndex++]);
						numberOfEventsForEventType.append(
                                args[argsIndex++]);
					}

                    setNameValuePair(nameValues[0],
                            eventNotificationParmsAndTypes[
                                       EN_NUM_OF_EV_TYPS_INDEX+1][0],
                            eventTypes.append("}").toString(),
                            eventNotificationParmsAndTypes[
                                    EN_NUM_OF_EV_TYPS_INDEX+1][1]);
					
                    setNameValuePair(nameValues[0],
                            eventNotificationParmsAndTypes[
                                       EN_NUM_OF_EV_TYPS_INDEX+2][0],
                            numberOfEventsForEventType.append("}").toString(),
                            eventNotificationParmsAndTypes[
                                    EN_NUM_OF_EV_TYPS_INDEX+2][1]);

                    parmIndex += EN_NUM_OF_EV_TYPS_ATTRS+1;
				}
				else 
				if (argsIndex == numEventSummariesIndex)
				{
					argsIndex++;
					HashMap<String, String> tmp =
						(HashMap<String, String>) nameValues[0].clone();
					for (int j = 0; j < numEventSummaries; 
								j++, i+=EN_NUM_OF_EV_SUMRY_ATTRS)
					{
						if (j > 0)
						{
							nameValues[j]= 
								new HashMap<String, String>();
							
							nameValues[j].putAll(tmp);

						}
						for (int k=0; k < EN_NUM_OF_EV_SUMRY_ATTRS; k++)
						{
                            setNameValuePair(nameValues[j], 
                                    eventNotificationParmsAndTypes[
                                           EN_NUM_OF_EV_SUMRY_INDEX+1+k][0],
                                    args[argsIndex++], 
                                    eventNotificationParmsAndTypes[
                                           EN_NUM_OF_EV_SUMRY_INDEX+1+k][1]);
						}
					}
					parmIndex += EN_NUM_OF_EV_SUMRY_ATTRS+1;
				}
				else
				{
					setNameValuePair(nameValues[0], 
							     eventNotificationParmsAndTypes[parmIndex][0],
							     args[argsIndex++], 
							     eventNotificationParmsAndTypes[parmIndex][1]);
					parmIndex++;
				}
				
			}
			
			for (int i = 0; i < numEventSummaries; i++)
			{
                if (i > 0)
                {
                    nameValues[i].put(EN_EVENT_SUMMARY_STRING,
                                        nameValues[0].get(EN_EVENT_SUMMARY_STRING));
                    nameValues[i].put(CONTROLLER_DEEP_LINK_URL,
                                    nameValues[0].get(CONTROLLER_DEEP_LINK_URL));
                    nameValues[i].put(EN_EVENT_SUMMARY_ID,
                                    nameValues[0].get(EN_EVENT_SUMMARY_ID));
                }

				completeDeepLinkUrl(nameValues[i]);

                long alertTime = (df.parse(nameValues[i].get(EN_EVENT_SUMMARY_TIME))).getTime()/1000;

                BoundaryAnnotation boundaryAnnotation =
                        new BoundaryAnnotation(APPD_BIZ_IMPACT_DETECTED,
                                               nameValues[i].get(EN_EVENT_SUMMARY_TYPE),
                                               alertTime,
                                               alertTime,
                                               "EventSummaryId="+nameValues[i].get(EN_EVENT_SUMMARY_ID),
                                               nameValues[i].get(CONTROLLER_DEEP_LINK_URL),
                                               nameValues[i].get(EN_EVENT_SUMMARY_STRING),
                                               nameValues[i].get(TAG));

                logger.info("JSON string is:"+boundaryAnnotation.toJSONString());
				RESTClient.sendPost(boundaryURL, boundaryAnnotation.toJSONString(), apiKey);
                Thread.sleep(1000);
			}
		} 
		catch (Exception e)
		{
			logger.error(e);
			return false;
		} 
		
		return true;
	}

	/**
	 * This method will send an policy violation notification received from 
	 * AppDynamics to Boundary
	 * 
	 * @param args - the list of arguments received with the violation 
	 * notification
	 * 
	 * @return true if all goes well, false otherwise
     */
	private static boolean sendPolicyViolationNotification(String args[])
	{
        int numEvaluationEntities =0;
		try
        {
            numEvaluationEntities = Integer.parseInt(
                    args[PVN_NUM_OF_EVAL_ENTITIES_INDEX+1]);
        }
        catch(NumberFormatException nfe)
        {
            logger.error("Unable to parse numEvaluationEntities from:"+args[PVN_NUM_OF_EVAL_ENTITIES_INDEX+1], nfe);

        }
		int	numParms = PVN_NUM_OF_EVAL_ENTITIES_INDEX
				+1+(numEvaluationEntities*PVN_NUM_OF_EVAL_ENTITIES_ATTRS)+2,
			argsIndex = 1,
			parmIndex=0;

		try
		{
			HashMap<String,String>[] nameValues =
									new HashMap[numEvaluationEntities*50];

			nameValues[0]= new HashMap<String, String>();

			while (argsIndex < args.length)
			{
				if ((argsIndex-1) == PVN_NUM_OF_EVAL_ENTITIES_INDEX)
				{
					HashMap<String,String> tmp =
						(HashMap<String,String>) nameValues[0].clone();
					argsIndex++;
					for (int j = 0; j < numEvaluationEntities; j++)
					{

						if (j > 0)
						{
							nameValues[j]= 
								new HashMap<String,String>();
							
							nameValues[j].putAll(tmp);

						}

						for (int k=0; k < PVN_NUM_OF_EVAL_ENTITIES_ATTRS; k++)
						{

							if ((PVN_NUM_OF_EVAL_ENTITIES_INDEX+k+2) == 
								PVN_NUM_OF_TRIG_CONDS_INDEX)
								{
									argsIndex++;

									int numTriggeredConditions = 0;
                                    try
                                    {
										numTriggeredConditions=Integer.parseInt(args[argsIndex]);
                                    }
                                    catch(NumberFormatException nfe)
                                    {
                                        logger.error("Unable to parse numTriggeredConditions from:"
                                                +args[argsIndex], nfe);

                                    }
									numParms += PVN_NUM_OF_TRIG_CONDS_ATTRS+1;
									HashMap<String,String> tmp2
									  =	(HashMap<String,String>)
												nameValues[0].clone();
									argsIndex++;
									int l;
									for (l = 0; l < numTriggeredConditions; l++)
									{

										if (l > 0)
										{
											nameValues[l]= 
												new HashMap<String,String>();
											
											nameValues[l].putAll(tmp2);

										}

										for (int m=0; 
												 m<PVN_NUM_OF_TRIG_CONDS_ATTRS; 
												 m++)
										{
											setNameValuePair(nameValues[l], 
											   pvnParmsAndTypes[
					                            PVN_NUM_OF_TRIG_CONDS_INDEX+1+m]
					                            							[0],
					                           args[argsIndex++], 
					                           pvnParmsAndTypes[
					                            PVN_NUM_OF_TRIG_CONDS_INDEX+1+m]
					                            						[1]);
											if ((PVN_NUM_OF_TRIG_CONDS_INDEX+1+m) 
													== 
											   PVN_TC_CONDITION_UNIT_TYPE_INDEX) 
											{
												if (!args[argsIndex-1].startsWith(
																		BASELINE_PREFIX))
												{
													m+=PVN_NUMBER_OF_BASELINE_PARMS;
													numParms -=3;
                                                }
												else if (args[argsIndex].equalsIgnoreCase(
															TRUE))
												{
													m+=PVN_NUMBER_OF_BASELINE_PARMS-1;
													numParms -=2;
                                                }
											}
										}
									}
									
								}
								else
								{
									
			                        setNameValuePair(nameValues[j], 
			                                  pvnParmsAndTypes[
			                                     PVN_NUM_OF_EVAL_ENTITIES_INDEX+1+k][0],
			                                  args[argsIndex++], 
			                                  pvnParmsAndTypes[
			                                     PVN_NUM_OF_EVAL_ENTITIES_INDEX+1+k][1]
			                                  );
								}
							}
					}
					
					parmIndex += (PVN_NUM_OF_EVAL_ENTITIES_ATTRS+PVN_NUM_OF_TRIG_CONDS_ATTRS+2);

				}
				else
				{
					setNameValuePair(nameValues[0], 
							pvnParmsAndTypes[parmIndex][0],
							args[argsIndex++], 
							pvnParmsAndTypes[parmIndex][1]);
					parmIndex++;
				}
				
			}

			for (int i = 0; nameValues[i] != null; i++)
			{
				if (i > 0)
				{
					nameValues[i].put(PVN_SUMMARY_MESSAGE,
                                        nameValues[0].get(PVN_SUMMARY_MESSAGE));
                    nameValues[i].put(CONTROLLER_DEEP_LINK_URL,
                                    nameValues[0].get(CONTROLLER_DEEP_LINK_URL));
                    nameValues[i].put(PVN_INCIDENT_ID,
                                    nameValues[0].get(PVN_INCIDENT_ID));
				}
				
				completeDeepLinkUrl(nameValues[i]);
                long alertTime = (df.parse(nameValues[i].get(PVN_ALERT_TIME))).getTime()/1000;

                BoundaryAnnotation boundaryAnnotation =
                        new BoundaryAnnotation(APPD_BIZ_IMPACT_DETECTED,
                                               nameValues[i].get(PVN_RULE_NAME),
                                               alertTime,
                                               alertTime,
                                               "Incident="+nameValues[i].get(PVN_INCIDENT_ID),
                                               nameValues[i].get(CONTROLLER_DEEP_LINK_URL),
                                               nameValues[i].get(PVN_SUMMARY_MESSAGE),
                                               nameValues[i].get(TAG));

                logger.info("JSON string is:"+boundaryAnnotation.toJSONString());

				RESTClient.sendPost(boundaryURL,boundaryAnnotation.toJSONString(), apiKey);
                Thread.sleep(1000);
			}
			
		} 
		catch (Exception e)
		{
			logger.error(e);
			return false;
		}
		
		return true;
	}

    static void removeDoubleQuotes(String[] args)
    {

        for (int i=0; i < args.length; i++)
        {
            args[i]=args[i].replaceAll("\"", "");
        }
    }

	/**
	 * Main method called from AppDynamics custom action shell script 
	 * or batch file.
	 * 
	 * @param args - arguments passed
	 */
	public static void main(String[] args) 
	{
		if (args.length < 10)
		{
			logger.error("Too few arguments"+
									" ... exiting");
			System.exit(-1);
		}

		
		logger.info(BANNER);
		logger.info("Received notification parameters:"+
				Arrays.toString(args));

        removeDoubleQuotes(args);

		int rc = 0;

		try 
		{
			String notificationType;
			boolean connectionSetup = setupConnection();
		
			if (!connectionSetup)
			{
				logger.error("Unable to setup connection");
				System.exit(-1);
			}
	
			notificationType = args[0];
			
			if (notificationType.equalsIgnoreCase(EVENT_NOTIFICATION))
			{
				logger.info("*** Processing event notification from AppDynamics");
				rc = sendEventNotification(args) ? 0 : -1;
			}
			else 
			if(notificationType.equalsIgnoreCase(POLICY_VIOLATION_NOTIFICATION))
			{
				logger.info("*** Processing policy violation notification from"+
									" AppDynamics");				
				rc = sendPolicyViolationNotification(args) ? 0 : -1;
			}
			else
			{
				throw new IllegalArgumentException(notificationType);
			}
			
			teardownConnection();

			logger.info(BANNER);				
		} 
		catch (Throwable t) 
		{
			logger.error(t);
		}
		
		System.exit(rc);
	}//main

}
