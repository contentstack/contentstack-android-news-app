# NewsApp-Android
Sample News app showing use of Contentstack SDK.

![Screen1][1]

## Content Modelling in Contentstack (Content Type)
In this news application, we will create 2 Content Types viz., Category and News. Let's see how to create it in Contentstack. Download the JSON format of Category and News content type and import it in Contentstack.
![Category JSON][6], ![News JSON][7].

To learn more about how to import content type, check out the guide.

Create **Category** Content Type

![Category_CT][2]

Create **News** Content Type

![News_CT][3]

## Clone repository

Open Terminal (for Mac and Linux users) or the command prompt (for Windows users) and paste the below command to clone the project.

    $ git clone https://github.com/raweng/NewsApp-Android.git


##  Usage
##### SDK Initialization
Grab API Key and Access Token from Contentstack management screen.

        Stack stack = Contentstack.stack(context, "siteApiKey","accessToken","production");

##### Query News Items
Home page shows list of top news that we have created in Contentstack. Let’s see how to query Contentstack. 

        Query query = stack.contentType("news").query();
        
        //filter topnews
        query.where("top_news", true)
        
        query.where("category", categoryUid);
        query.includeReference("category");
        query.ascending("updated_at");
        
        query.find(new QueryResultsCallBack() {
            @Override
            public void onCompletion(ResponseType resType, QueryResult result, Error error) {
                if (error == null) {
                    //Success block
                    ArrayList<Entry> entries = (ArrayList<Entry>) result.getResultObjects();
                } else {
                    //Error block 
                    //provides more details about error
                }
            }
        });

For more details about Query, refer [Contentstack Query Guide][5]

#### Filter By Category
        //categoryUid is a variable containing selected category uid
        query.where("category", categoryUid);

#### Filter By Language 
        //For English language
        query.language(Language.ENGLISH_UNITED_STATES.name())
    
        //For Hindi language
        query.language(Language.HINDI_INDIA.name())
    


  [1]: https://api.contentstack.io/v2/assets/566ad5bd24349fdd77167988/download?uid=blte3fa016ec4c2af0b&AUTHTOKEN=bltefb4f32b56206d8e5bc6cb9e
  [2]: https://api.contentstack.io/v2/assets/56b85f310ea5e91f35d9ffbb/download?uid=blt0ef50bfc28445d08&AUTHTOKEN=bltbfb694c915ad7c3b24584a7b
  [3]: https://api.contentstack.io/v1/uploads/56b85f390ea5e91f35d9ffc6/download?uid=blt04d8d8e7c7c632c5&AUTHTOKEN=bltefb4f32b56206d8e5bc6cb9e
  [4]: http://contentstackandroidsdk.builtapp.io/cs_android_quickstart.zip
  [5]: http://csdocs.builtapp.io/developer/android/query-guide
  [6]: https://www.dropbox.com/s/p8ybnoa3cqz3nki/category.json?dl=1
  [7]: https://www.dropbox.com/s/c7ls6xee87hwa0b/news.json?dl=1
