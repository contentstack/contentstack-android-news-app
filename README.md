# NewsApp-Android
Sample News app showing use of Contentstack SDK.

<img src='http://contentstackdocs.built.io/images/ios-screentshot-new.jpg' width='650' height='550'/>

## Content Modelling in Contentstack (Content Type)
In this news application, we will create 2 Content Types viz., Category and News. Download the JSON format of Category and News content type and import it in Contentstack.

[Category JSON](http://contentstackdocs.built.io/json/category.json)

[News JSON](http://contentstackdocs.built.io/json/news.json).

To learn more about how to import content type, check out the [guide](http://contentstackdocs.built.io/developer/guides/content-types#import-a-content-type).

Create **Category** Content Type

<img src='http://contentstackdocs.built.io/images/CS_CT.png' width='600' height='400'/>

Create **News** Content Type

<img src='http://contentstackdocs.built.io/images/Cs-News-CT.png' width='600' height='550'/>


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
    
  [5]: http://contentstackdocs.built.io/developer/android/query-guide
