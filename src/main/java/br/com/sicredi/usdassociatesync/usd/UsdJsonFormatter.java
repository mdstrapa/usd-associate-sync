package br.com.sicredi.usdassociatesync.usd;
import com.google.gson.Gson;

public class UsdJsonFormatter {
    

    public String formatObjectResponse(String originalResponseJSON, String usdObject){
        //{"rest_access":
        //{"in":
        //{"chg":
        int charactersToRemoveFromBeginning = usdObject.length() + 4;
        String formattedJson;
        formattedJson = originalResponseJSON.substring(0, originalResponseJSON.length() - 1)
            .replace("@COMMON_NAME", "COMMON_NAME")
            .replace("@id", "id")
            .replace("@REL_ATTR", "REL_ATTR");
        return formattedJson.substring(charactersToRemoveFromBeginning, formattedJson.length());
    }

    public String formatListResponse(String originalResponseJSON){
        String formattedJson;
        formattedJson = originalResponseJSON
            .replace("@COMMON_NAME", "COMMON_NAME")
            .replace("@id", "id")
            .replace("@REL_ATTR", "REL_ATTR");

        
        formattedJson = formattedJson.substring(formattedJson.indexOf("["));
        return formattedJson.substring(0, formattedJson.lastIndexOf("]") + 1);
    }

    public String formatRequestBodyForCreation(Object objectToFormat, String usdObject){

        Gson gson = new Gson();
        String requestBody = gson.toJson(objectToFormat)
            .concat("}")
            .replace("REL_ATTR", "@REL_ATTR")
            .replace("COMMON_NAME", "@COMMON_NAME")
            .replace("\"id","\"@id")
            .replace("\\u0027","'");
        
        requestBody = "{\"" + usdObject + "\" : ".concat(requestBody);

        return requestBody;
    }

    public String formatRequestBodyForUpdate(Object objectToFormat, String usdObject){

        Gson gson = new Gson();
        String requestBody = gson.toJson(objectToFormat)
            .concat("}")
            .replace("REL_ATTR", "@REL_ATTR")
            .replace("COMMON_NAME", "@COMMON_NAME")
            .replace("\"id","\"@id");
        
        requestBody = "{\"" + usdObject + "\" : ".concat(requestBody);

        return requestBody;
    }

    public String getCommonNameFromResponse(String responseJson){
        return responseJson.substring(
            responseJson.indexOf(":", responseJson.indexOf("@COMMON_NAME")) + 1,
            responseJson.indexOf(",", responseJson.indexOf(":", responseJson.indexOf("@COMMON_NAME"))))
            .trim();
    }

    public String getRellAttrFromResponse(String responseJson){
        return responseJson.substring(
            responseJson.indexOf(":", responseJson.indexOf("@REL_ATTR")) + 2, 
            responseJson.indexOf(",", responseJson.indexOf(":", responseJson.indexOf("@REL_ATTR"))) - 1).
            trim();
    }

    public String getObjectFromResponse(String responseJson){
        return responseJson.substring(2, responseJson.indexOf(":")-1).trim();
    }

    public Integer getTotalCountFromQuery(String responseJson){
        // Integer totalCount = 0;
        // totalCount = Integer.parseInt(resposeJson.substring(resposeJson.indexOf("@TOTAL_COUNT") + 3, 1));
        // return totalCount;
        return Integer.parseInt(responseJson.substring(
            responseJson.indexOf("@TOTAL_COUNT")+14,
            responseJson.indexOf("@TOTAL_COUNT")+15)
            .trim());

    }
}
