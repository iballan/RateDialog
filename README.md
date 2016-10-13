RateDialog
==============

[![](https://jitpack.io/v/iballan/RateDialog.svg)](https://jitpack.io/#iballan/RateDialog)

## RateDialog will allow you to prompt a message to users helping them to Rate you app

Screenshots:
--------


Usage :

XML Layout:
``` xml
// No XML
```

Java:
``` java
	RateDialog.Config config = new RateDialog.Config();
	config.setInstallDays(2); // after installation with 7 days show it
	config.setLaunchTimes(2); // after launch times show it
	config.setMessage(R.string.rate_message);
	config.setmNoThanks(R.string.rate_no_thanks);
	config.setmOkButton(R.string.rate_ok);
	config.setmRemindMeLater(R.string.rate_remind_me);
	config.setTitle(R.string.rate_title);
	return RateDialog.onStart(config, context);
```

Install
--------

You can install using Gradle:

```gradle
	repositories {
	    maven { url "https://jitpack.io" }
	}
	dependencies {
	    compile 'com.github.iballan:RateDialog:0.0.1'
	}
```

Contact me:
--------

Twitter: [@mbh01t](https://twitter.com/mbh01t)

Github: [iballan](https://github.com/iballan)

Website: [www.mbh01.com](http://mbh01.com)

Credits:
--------

MaterialDialog : https://github.com/drakeet/MaterialDialog


License
--------

    Copyright 2016 Mohamad Ballan.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.