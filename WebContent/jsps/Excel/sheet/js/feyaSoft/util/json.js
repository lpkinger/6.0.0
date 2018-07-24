/*
    json.js
    2007-03-06

	modified by yatrack.cn 2007-03-18
	
    Public Domain

    This file adds these methods to JavaScript:

        array.toJSONString()
        boolean.toJSONString()
        date.toJSONString()
        number.toJSONString()
        object.toJSONString()
        string.toJSONString()
            These methods produce a JSON text from a JavaScript value.
            It must not contain any cyclical references. Illegal values
            will be excluded.

            The default conversion for dates is to an ISO string. You can
            add a toJSONString method to any date object to get a different
            representation.

        string.parseJSON(filter)
            This method parses a JSON text to produce an object or
            array. It can throw a SyntaxError exception.

            The optional filter parameter is a function which can filter and
            transform the results. It receives each of the keys and values, and
            its return value is used instead of the original value. If it
            returns what it received, then structure is not modified. If it
            returns undefined then the member is deleted.

            Example:

            // Parse the text. If a key contains the string 'date' then
            // convert the value to a date.

            myData = text.parseJSON(function (key, value) {
                return key.indexOf('date') >= 0 ? new Date(value) : value;
            });

    It is expected that these methods will formally become part of the
    JavaScript Programming Language in the Fourth Edition of the
    ECMAScript standard in 2008.
*/

/*
    The global object JSON contains two methods.

    JSON.stringify(value) takes a JavaScript value and produces a JSON text.
    The value must not be cyclical.

    JSON.parse(text) takes a JSON text and produces a JavaScript value. It will
    return false if there is an error.
*/

var JSON = function ()
{
	var m = {
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        };
	var s = {
		'boolean': function (x) {
			return String(x);
		},
		
		'date':	function (x) {
			// Ultimately, this method will be equivalent to the date.toISOString method.
	        function f(n) {
				// Format integers to have at least two digits.
	            return n < 10 ? '0' + n : n;
	        };

	        return '"' + x.getFullYear() + '-' +
	                f(x.getMonth() + 1) + '-' +
	                f(x.getDate()) + 'T' +
	                f(x.getHours()) + ':' +
	                f(x.getMinutes()) + ':' +
	                f(x.getSeconds()) + '"';
    	},
		
		'number': function (x) {
			// JSON numbers must be finite. Encode non-finite numbers as null.
        	return isFinite(x) ? String(x) : "null";
    	},
		
		'object':function (x) {
	        var a = ['{'],  // The array holding the text fragments.
	            b,          // A boolean indicating that a comma is required.
	            k,          // The current key.
	            v;          // The current value.
	
	        function p(s) {
				// p accumulates text fragment pairs in an array. It inserts a comma before all
				// except the first fragment pair.
	            if (b) {
	                a.push(',');
	            }
	            a.push(JSON.stringify(k), ':', s);
	            b = true;
	        };

			// Iterate through all of the keys in the object, ignoring the proto chain.
	        for (k in x) {
	            if (x.hasOwnProperty(k)) {
	                v = x[k];
	                switch (typeof v) {
	
					// Values without a JSON representation are ignored.
	
	                case 'undefined':
	                case 'function':
	                case 'unknown':
	                    break;
	
					// Serialize a JavaScript object value. Ignore objects that lack the
					// toJSONString method. Due to a specification error in ECMAScript,
					// typeof null is 'object', so watch out for that case.
	
	                case 'object':
	                    if (v) {
							/*
	                        if (typeof v.toJSONString === 'function') {
	                            p(v.toJSONString());
	                        }
	                        */
							p(JSON.stringify(v));
	                    } else {
	                        p("null");
	                    }
	                    break;
	                default:
	                    p(JSON.stringify(v));
	                }
	            }
	        }
			// Join all of the fragments together and return.
        	a.push('}');
        	return a.join('');
    	},
		
		'array':function (x) {
	        var a = ['['],  // The array holding the text fragments.
	            b,          // A boolean indicating that a comma is required.
	            i,          // Loop counter.
	            l = x.length,
	            v;          // The value to be stringified.
	
	        function p(s) {
				// p accumulates text fragments in an array. It inserts a comma before all
				// except the first fragment.
	            if (b) {
	                a.push(',');
	            }
	            a.push(s);
	            b = true;
	        };

			// For each value in this array...
	        for (i = 0; i < l; i += 1) {
	            v = x[i];
	            switch (typeof v) {
	
				// Values without a JSON representation are ignored.
	
	            case 'undefined':
	            case 'function':
	            case 'unknown':
	                break;
	
				// Serialize a JavaScript object value. Ignore objects thats lack the
				// toJSONString method. Due to a specification error in ECMAScript,
				// typeof null is 'object', so watch out for that case.
	            case 'object':
	                if (v) {
	                    /*
                        if (typeof v.toJSONString === 'function') {
                            p(v.toJSONString());
                        }
                        */
						p(JSON.stringify(v));
	                } else {
	                    p("null");
	                }
	                break;
	
				// Otherwise, serialize the value.
	
	            default:
	                p(JSON.stringify(v));
	            }
	        }
	
			// Join all of the fragments together and return.
	        a.push(']');
	        return a.join('');
	    },
		
		'string': function (x) {
			// If the string contains no control characters, no quote characters, and no
			// backslash characters, then we can simply slap some quotes around it.
			// Otherwise we must also replace the offending characters with safe
			// sequences.
            if (/["\\\x00-\x1f]/.test(x)) {
                return '"' + x.replace(/([\x00-\x1f\\"])/g, function(a, b) {
                    var c = m[b];
                    if (c) {
                        return c;
                    }
                    c = b.charCodeAt();
                    return '\\u00' +
                        Math.floor(c / 16).toString(16) +
                        (c % 16).toString(16);
                }) + '"';
            }
            return '"' + x + '"';
        }	
	};
	
	return {
		copyright: '(c)2005 JSON.org',
        license: 'http://www.JSON.org/license.html',
		
		/*
    		Stringify a JavaScript value, producing a JSON text.
		*/
        stringify: function (v) {
			//alert("typeof v:"+typeof v);
			var vType = typeof(v);
			if(vType === 'object')
			{
				if(v instanceof Date)
				{
					vType = 'date';
				}
				else if(v instanceof Array)
				{
					vType = 'array';
				}
			}
			//alert(vType);
            var f = s[vType];
			//alert(f);
            if (f) {
                v = f(v);
                if (typeof v == 'string') {
                    return v;
                }
            }
            return null;
        },
		
		/*
		    Parse a JSON text, producing a JavaScript value.
		    It returns false if there is a syntax error.
		*/
        parse: function (text) {
			//alert("text == " + text);
            try {
                return !(/[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(
                        text.replace(/"(\\.|[^"\\])*"/g, ''))) &&
                    eval('(' + text + ')');
            } catch (e) {
                return false;
            }
        }
    };
}();
