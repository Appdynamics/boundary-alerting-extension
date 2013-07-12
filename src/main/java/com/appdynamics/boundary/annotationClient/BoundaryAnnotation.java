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
 *  This class will format the AppDynamics alert in the form of an annotation
 *  to be sent to Boundary.
 *
 * Copyright (c) AppDynamics, Inc.
 * @author Pranta Das
 * Created on: September 18, 2012.
 */

public class BoundaryAnnotation
{
    String type;
    String subType;
    long startTime;
    long endTime;
    String linkRel;
    String linkRef;
    String linkNote;
    String tag;

    public BoundaryAnnotation(String _type,
                              String _subType,
                              long _startTime,
                              long _endTime,
                              String _linkRel,
                              String _linkRef,
                              String _linkNote,
                              String _tag)
    {
        type = _type;
        subType = _subType;
        startTime = _startTime;
        endTime = _endTime;
        linkRel = _linkRel;
        linkRef = _linkRef;
        linkNote = _linkNote;
        tag = _tag;
    }

    public String toJSONString()
    {
        StringBuilder jsonString = new StringBuilder();

        jsonString.append("{\n\t")
                   .append("\"type\": \"").append(type).append("\",\n\t")
                   .append("\"sub-type\": \"").append(subType).append("\",\n\t")
                   .append("\"start_time\": ").append(startTime).append(",\n\t")
                   .append("\"end_time\": ").append(endTime).append(",\n\t")
                   .append("\"links\": [\n\t\t{\t\n\t\t\t")
                   .append("\"note\": \"").append(linkNote).append("\",\n\t\t\t")
                   .append("\"href\": \"").append(linkRef).append("\",\n\t\t\t")
                   .append("\"rel\": \"").append(linkRel).append("\"\n\t\t}\n\t],\n\t")
                   .append("\"tags\": [\"").append(tag).append("\"]\n}\n");

        return jsonString.toString();
}

}
