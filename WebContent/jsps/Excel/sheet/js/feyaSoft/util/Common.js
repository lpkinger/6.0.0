/**
 * Copyright(c) 2006-2009, FeyaSoft Inc. All right reserved
 * ====================================================================
 * Licence @ Feyasoft Inc.
 * ====================================================================
 */
Ext.ns("Ext.util");

/**
 * This JS is used to define util functions
 *
 * @author fzhuang
 * @Date July 26, 2007
 */
Ext.util.Common = Ext.util.Common || {
	/*
	 * get day offset between 2 date
	 */
	getDayOffset : function(sdate, edate){
		sdate = sdate.clearTime(true);
		edate = edate.clearTime(true);
		var dayoff = Math.round(edate.getElapsed(sdate)/(3600*24*1000))+1;
		return dayoff;
	},
	/*
	 * get the size of a object
	 */
	getSizeOfObj : function(o){
		var len = 0;
		for(var p in o){
			len++;
		}
		return len;
	},
	
	/*
	 * compare whether 2 object is equal
	 */
	isEqualObj : function(o1, o2){
		if('object' == Ext.type(o1) && 'object' == Ext.type(o2)){
			if(Ext.util.Common.getSizeOfObj(o1) == Ext.util.Common.getSizeOfObj(o2)){
				for(var p in o1){
					if('object' == Ext.type(o1[p]) || 'array' == Ext.type(o1[p])){
						if(!Ext.util.Common.isEqualObj(o1[p], o2[p])){
							return false;
						}
					}else if(o1[p] != o2[p]){
						return false;
					}
				}
			}else{
				return false;
			}
		}else if('array' == Ext.type(o1) && 'array' == Ext.type(o1)){
			if(o1.length != o2.length){
				return false;
			}else{
				for(var i = 0, len = o1.length; i < len; i++){
					if(!Ext.util.Common.isEqualObj(o1[i], o2[i])){
						return false;
					}
				}
			}
		}else if(o1 != o2){
			return false;
		}
		return true;
	},

    /**
     * validate url ...
     * @param {} url
     * @return {Boolean}
     */
    validateURL : function(url)
    {
        if (url == null) return false;
        
        var myURL = url.trim();
        if(myURL.length != 0)
        {
            if (myURL)
                var j = new RegExp();
                j.compile("^[A-Za-z]+://[A-Za-z0-9-]+\.[A-Za-z0-9]+");
                if (j.test(myURL))
                {
                    return true;
                }
        } else {
                return true;
        }

        return false;
    },

    isUrl : function (url, needHead) {
        if (url == null) return false;
	var regexp = /(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
        if (needHead) regexp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
	return regexp.test(url);
    },

    /**
     * Array to string with separator
     */
    arrToStringBySep : function(myArray, separator) {
     return myArray.join(separator);
    },

	/** 
	 * truncate Custom renderer function
	 * val rendered 
	 * @param {Object} val
	 */
     getDate : function(days){
         var myDate = new Date();
         myDate.setDate(myDate.getDate()+days);
         return myDate;
     },

	/** 
	 * truncate Custom renderer function
	 * val rendered 
	 * @param {Object} val
	 */
     truncate : function(val, p, record){
         if (val.length > 20) {
             p.attr = 'ext:qtip=' + '"' + val + '"';
             val = val.substring(0, 20);
             val = val.replace(/\w+$/, '') + '...';
         }
         return val;
     },
     
     /** 
     * Red/Green Custom renderer function
     * renders red if <0 otherwise renders green 
     * @param {Object} val
     */
     renderPosNeg : function(val){
         if(val >= 0){
             return '<span style="color:green;">' + val + '</span>';
         }else if(val < 0){
             return '<span style="color:red;">' + val + '</span>';
         }
         return val;
     },
     
     /** 
      * Percent Custom renderer function
      * Renders red or green with %
      * @param {Object} val
      */
      renderPctChange : function(val){
          if(val >= 0){
               return '<span style="color:green;">' + val + '%</span>';
          }else if(val < 0){
               return '<span style="color:red;">' + val + '%</span>';
          }
          return val;
     },

    /**
     * Transfer boolean fromat
     * @method formatBoolean
     * @param {any} obj The object being testing
     * @return yes/no
     */    
    formatBoolean: function(obj){
        return obj ? 'Yes' : 'No';  
    },
    
    /**
     * Determines whether or not the provided object is a boolean
     * @method isBoolean
     * @param {any} obj The object being testing
     * @return Boolean
     */
    isBoolean: function(obj) {
        return typeof obj == 'boolean';
    },
    
    /**
     * Determines whether or not the provided object is a function
     * @method isFunction
     * @param {any} obj The object being testing
     * @return Boolean
     */
    isFunction: function(obj) {
        return typeof obj == 'function';
    },
        
    /**
     * Determines whether or not the provided object is null
     * @method isNull
     * @param {any} obj The object being testing
     * @return Boolean
     */
    isNull: function(obj) {
        return obj === null;
    },
        
    /**
     * Determines whether or not the provided object is a legal number
     * @method isNumber
     * @param {any} obj The object being testing
     * @return Boolean
     */
    isNumber: function(obj) {
        return typeof obj == 'number' && isFinite(obj);
    },
        
    /**
     * Determines whether or not the provided object is a string
     * @method isString
     * @param {any} obj The object being testing
     * @return Boolean
     */
    isString: function(obj) {
        return typeof obj == 'string';
    },
        
    /**
     * Determines whether or not the provided object is undefined
     * @method isUndefined
     * @param {any} obj The object being testing
     * @return Boolean
     */
    isUndefined: function(obj) {
        return typeof obj == 'undefined';
    },
    
    // generates a renderer function to be used for textual date groups
    textDate : function(){
        // create the cache of ranges to be reused
        var today = new Date().clearTime(true);
        var year = today.getFullYear();
        var todayTime = today.getTime();
        var yesterday = today.add('d', -1).getTime();
        var tomorrow = today.add('d', 1).getTime();
        var weekDays = today.add('d', 6).getTime();
        var lastWeekDays = today.add('d', -6).getTime();

        return function(date){
            if(!date) {
                return '(No Date)';
            }
            var notime = date.clearTime(true).getTime();

            if (notime == todayTime) {
                return 'Today';
            }
            if(notime > todayTime){
                if (notime == tomorrow) {
                    return 'Tomorrow';
                }
                if (notime <= weekDays) {
                    return date.format('l');
                }
            }else {
            	if(notime == yesterday) {
                	return 'Yesterday';
	            }
	            if(notime >= lastWeekDays) {
	                return 'Last ' + date.format('l');
	            }
            }            
            return date.getFullYear() == year ? date.format('D m/d') : date.format('D m/d/Y');
       }
    },

    countryStore : function() {
            var countryStore = ["AFGHANISTAN","ALBANIA","ALGERIA","AMERICAN SAMOA","ANDORRA","ANGOLA","ANGUILLA","ANTARCTICA","ANTIGUA AND BARBUDA","ARGENTINA",
"ARMENIA","ARUBA","AUSTRALIA","AUSTRIA","AZERBAIJAN","BAHAMAS","BAHRAIN","BANGLADESH","BARBADOS","BELARUS"
,"BELGIUM","BELIZE","BENIN","BERMUDA","BHUTAN","BOLIVIA","BOSNIA AND HERZEGOWINA",
"BOTSWANA","BOUVET ISLAND","BRAZIL","BRITISH INDIAN OCEAN TERRITORY","BRUNEI DARUSSALAM","BULGARIA","BURKINA FASO","BURUNDI","CAMBODIA","CAMEROON","CANADA",
"CAPE VERDE","CAYMAN ISLANDS","CENTRAL AFRICAN REPUBLIC","CHAD","CHILE","CHINA","CHRISTMAS ISLAND","COCOS(KEELING) ISLANDS","COLOMBIA","COMOROS","CONGO","CONGO, THE DEMOCRATIC REPUBLIC OF THE","COOK ISLANDS","COSTA RICA","COTE D'IVOIRE","CROATIA (localname: Hrvatska)","CUBA","CYPRUS",
"CZECH REPUBLIC","DENMARK","DJIBOUTI","DOMINICA","DOMINICAN REPUBLIC","EAST TIMOR","ECUADOR","EGYPT","EL SALVADOR","EQUATORIAL GUINEA",
"ERITREA","ESTONIA","ETHIOPIA","FALKLAND ISLANDS (MALVINAS)","FAROE ISLANDS","FIJI","FINLAND","FRANCE","FRANCE, METROPOLITAN","FRENCH GUIANA",
"FRENCH POLYNESIA","FRENCH SOUTHERN TERRITORIES","GABON","GAMBIA","GEORGIA","GERMANY","GHANA","GIBRALTAR","GREECE","GREENLAND","GRENADA",
"GUADELOUPE","GUAM","GUATEMALA","GUINEA","GUINEA-BISSAU","GUYANA","HAITI","HEARD AND MC DONALD ISLANDS","HOLY SEE (VATICAN CITY STATE)",
"HONDURAS","HONG KONG","HUNGARY","ICELAND","INDIA","INDONESIA","IRAN (ISLAMIC REPUBLIC OF)","IRAQ","IRELAND","ISRAEL","ITALY","JAMAICA","JAPAN","JORDAN","KAZAKHSTAN","KENYA","KIRIBATI","KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF","KOREA, REPUBLICOF","KUWAIT","KYRGYZSTAN",
"LAO PEOPLE'S DEMOCRATIC REPUBLIC","LATVIA","LEBANON","LESOTHO","LIBERIA","LIBYAN ARAB JAMAHIRIYA","LIECHTENSTEIN","LITHUANIA","LUXEMBOURG",
"MACAU","MACEDONIA, THE FORMER YUGOSLAV REPUBLIC","MADAGASCAR","MALAWI","MALAYSIA","MALDIVES","MALI","MALTA","MARSHALL ISLANDS","MARTINIQUE",
"MAURITANIA","MAURITIUS","MAYOTTE","MEXICO","MICRONESIA, FEDERATED STATES OF","MOLDOVA, REPUBLIC OF","MONACO","MONGOLIA","MONTSERRAT",
"MOROCCO","MOZAMBIQUE","MYANMAR (Burma)","NAMIBIA","NAURU","NEPAL","NETHERLANDS","NETHERLANDS ANTILLES","NEW CALEDONIA","NEW ZEALAND",
"NICARAGUA","NIGER","NIGERIA","NIUE","NORFOLK ISLAND","NORTHERN MARIANA ISLANDS","NORWAY","OMAN","PAKISTAN","PALAU","PANAMA","PAPUA NEW GUINEA",
"PARAGUAY","PERU","PHILIPPINES","PITCAIRN","POLAND","PORTUGAL","PUERTO RICO","QATAR","REUNION","ROMANIA","RUSSIAN FEDERATION",
"RWANDA","SAINT KITTS AND NEVIS","SAINT LUCIA","SAINT VINCENT AND THE GRENADINES","SAMOA","SAN MARINO","SAO TOME AND PRINCIPE",
"SAUDI ARABIA","SENEGAL","SEYCHELLES","SIERRA LEONE","SINGAPORE","SLOVAKIA (Slovak Republic)","SLOVENIA","SOLOMON ISLANDS","SOMALIA",
"SOUTH AFRICA","SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS","SPAIN","SRI LANKA","ST. HELENA","ST.PIERRE AND MIQUELON","SUDAN","SURINAME","SVALBARD AND JAN MAYEN ISLANDS","SWAZILAND","SWEDEN","SWITZERLAND","SYRIAN ARAB REPUBLIC","TAIWAN, REPUBLIC OF CHINA",
"TAJIKISTAN","TANZANIA, UNITED REPUBLIC OF","THAILAND","TOGO","TOKELAU","TONGA","TRINIDAD AND TOBAGO","TUNISIA","TURKEY","TURKMENISTAN",
"TURKS AND CAICOS ISLANDS","TUVALU","UGANDA","UKRAINE","UNITED ARAB EMIRATES","UNITED KINGDOM","UNITED STATES","UNITED STATES MINOR OUTLYING ISLANDS",
"URUGUAY","UZBEKISTAN","VANUATU","VENEZUELA","VIET NAM","VIRGIN ISLANDS (BRITISH)","VIRGIN ISLANDS (U.S.)","WALLIS AND FUTUNA ISLANDS",
"WESTERN SAHARA","YEMEN","YUGOSLAVIA (now Serbia and Montenegro)","ZAMBIA","ZIMBABWE"];
        return countryStore;
    },

    formatDetailDate: function(obj){
        return obj ? obj.dateFormat('D M d H:i') : '';
    },

    // This will reset the theme based on the main.gsp file
    // <link rel="stylesheet" type="text/css" title="theme-Blue"  href="${createLinkTo(dir:'js',file:'extjs/resources/css/xtheme-blue.css')}" />
    // <link rel="stylesheet" type="text/css" title="theme-Gray"  href="${createLinkTo(dir:'js',file:'extjs/resources/css/xtheme-gray.css')}" />
    setActiveTheme : function(theme) {
        var links = document.getElementsByTagName("link");
        var len = links.length;
        for (var i = 0; i < len; i++) {
            var a = links[i];
            if (a.getAttribute("rel").indexOf("style") != -1 && a.getAttribute("title") && a.getAttribute("title").indexOf("theme-") != -1) {
                a.disabled = true;
                if (a.getAttribute("title") == theme) a.disabled = false;
            }
        }
    },

    // for set wall paper
    setWallpicture : function(name) {
        document.body.style.backgroundImage =  "url(js/feyaSoft/desktop/wallpapers/" + name + ")";
    },

     getURLSearch: function (){
                var strSearch = location.search;
                var reg1 = /=/gi;
                var reg2 = /&/gi;
                strSearch = strSearch.substr(1, strSearch.length);
                strSearch = strSearch.replace(reg1, '":"');
                strSearch = strSearch.replace(reg2, '","');
                if (strSearch == "") 
                    return null;
                else 
                    strSearch = '{"' + strSearch + '"}';
                eval("var ArrayUrl=" + strSearch);
                return ArrayUrl;
    },

    getTimeZone : function() {

        var tmSummer = new Date(Date.UTC(2005, 6, 30, 0, 0, 0, 0));
        var so = -1 * tmSummer.getTimezoneOffset();
        var tmWinter = new Date(Date.UTC(2005, 12, 30, 0, 0, 0, 0));
        var wo = -1 * tmWinter.getTimezoneOffset();

        if (-660 == so && -660 == wo) return "2";
	if (-600 == so && -600 == wo) return "3";
	if (-570 == so && -570 == wo) return "4";
	if (-540 == so && -600 == wo) return "4";
	if (-540 == so && -540 == wo) return "4";
	if (-480 == so && -540 == wo) return "4";
	if (-480 == so && -480 == wo) return "5";
	if (-420 == so && -480 == wo) return "6";
	if (-420 == so && -420 == wo) return "10";
	if (-360 == so && -420 == wo) return "7";
	if (-360 == so && -360 == wo) return '12';
	if (-360 == so && -300 == wo) return '11';
	if (-300 == so && -360 == wo) return '15';
	if (-300 == so && -300 == wo) return '16';
	if (-240 == so && -300 == wo) return "17";
	if (-240 == so && -240 == wo) return '19';
	if (-240 == so && -180 == wo) return '20';
	if (-180 == so && -240 == wo) return '23';
	if (-180 == so && -180 == wo) return '25';
	if (-180 == so && -120 == wo) return '27';
	if (-150 == so && -210 == wo) return '26';
	if (-120 == so && -180 == wo) return '28';
	if (-120 == so && -120 == wo) return '30';
	if (-60 == so && -60 == wo) return '31';
	if (0 == so && -60 == wo) return '32';
	if (0 == so && 0 == wo) return '33';
	if (60 == so && 0 == wo) return '34';
	if (60 == so && 60 == wo) return '38';
	if (60 == so && 120 == wo) return '49';
	if (120 == so && 60 == wo) return '36';
	if (120 == so && 120 == wo) return '45';
	if (180 == so && 120 == wo) return '42';
	if (180 == so && 180 == wo) return '53';
	if (240 == so && 180 == wo) return '52';
	if (240 == so && 240 == wo) return '56';
	if (270 == so && 210 == wo) return '55';
	if (270 == so && 270 == wo) return '61';
	if (300 == so && 240 == wo) return '61';
	if (300 == so && 300 == wo) return '62';
	if (330 == so && 330 == wo) return '63';
	if (345 == so && 345 == wo) return '64';
	if (360 == so && 300 == wo) return '65';
	if (360 == so && 360 == wo) return '65';
	if (390 == so && 390 == wo) return '70';
	if (420 == so && 360 == wo) return '69';
	if (420 == so && 420 == wo) return '71';
	if (480 == so && 420 == wo) return '73';
	if (480 == so && 480 == wo) return '73';
	if (540 == so && 480 == wo) return '79';
	if (540 == so && 540 == wo) return '78';
	if (570 == so && 570 == wo) return '82';
	if (570 == so && 630 == wo) return '81';
	if (600 == so && 540 == wo) return '80';
	if (600 == so && 600 == wo) return '83';
	if (600 == so && 660 == wo) return '84';
	if (630 == so && 660 == wo) return '85';
	if (660 == so && 600 == wo) return '86';
	if (660 == so && 660 == wo) return '87';
	if (690 == so && 690 == wo) return '88';
	if (720 == so && 660 == wo) return '89';
	if (720 == so && 720 == wo) return '90';
	if (720 == so && 780 == wo) return '89';
	if (765 == so && 825 == wo) return '90';
	if (780 == so && 780 == wo) return '1'
	if (840 == so && 840 == wo) return "1";

        return 17;
    },

    // set in the cookies ...
    setCookie : function(name, value, days) {
          var date = new Date();
          date.setTime(date.getTime()+(days*24*60*60*1000));
          var thisCookie = name + "=" + escape(value) +
              ((days) ? "; expires=" + date.toGMTString() : "");
          document.cookie = thisCookie;
    },

    readCookie : function(name) {
           var nameSG = name + "=";
           if (document.cookie == null || document.cookie.indexOf(nameSG) == -1)
               return null;

           var ca = document.cookie.split(';');
           for(var i=0; i<ca.length; i++) {
               var c = ca[i];
               while (c.charAt(0)==' ') c = c.substring(1,c.length);
               if (c.indexOf(nameSG) == 0) return c.substring(nameSG.length,c.length);
           }

           return null;
     },

     eraseCookie : function(name) { Ext.util.Common.setCookie(name,"", 0.0001); }
};

///////////////////////////////////////////////
/// add more vtype ....
Ext.apply(Ext.form.VTypes, {
    'usernameVal': function(val, field) {
          var objRegExp  = /^[a-zA-Z][-_.@a-zA-Z0-9]{0,80}$/;
          return objRegExp.test(val);
    },
    'usernameValText': 'Username must start with a letter and only include -_.@a-zA-Z0-9 characters'

});
///////////////////////////////////////////////

String.prototype.ellipse = function(maxLength){
    if(this.length > maxLength){
        return this.substr(0, maxLength-3) + '...';
    }
    return this;
};
