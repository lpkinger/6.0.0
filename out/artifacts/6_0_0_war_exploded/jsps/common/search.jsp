<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css" />
<link rel="stylesheet" href="<%=basePath%>resource/css/search.css"
	type="text/css"></link>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<link rel="stylesheet" href="<%=basePath%>resource/css/upgrade/bluegray/main.css" type="text/css"></link>
<style type="text/css">
.x-grid-row .x-grid-cell {
	line-height:26px;
    height: 26px;
}
</style>
<script type="text/javascript">
	// ghost
	Ext.example = function() {
		var b;
		function a(t, c, d) {
			return '<div class="msg x-message-box-' + t + '"><h3>' + c + "</h3><p>" + d + "</p></div>"
		}
		return {
			msg : function(t, g, e, y) {
				if (!b) {
					b = Ext.DomHelper.insertFirst(document.body, {
						id : "msg-div"
					}, true);
				}
				var d = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 2));
				var c = Ext.DomHelper.append(b, a((t || 'info'), g, d), true);
				c.hide();
				c.slideIn("t").ghost("t", {
					delay : y || 1000,
					remove : true
				});
			},
			init : function() {
				if (!b) {
					b = Ext.DomHelper.insertFirst(document.body, {
						id : "msg-div"
					}, true);
				}
			}
		};
	}();
	Ext.onReady(Ext.example.init, Ext.example);
	// override remote queryStr length
	Ext.override(Ext.form.field.ComboBox, {
		initEvents: function() {
	        var me = this;
	        me.callParent();
	        if (!me.enableKeyEvents) {
	            me.mon(me.inputEl, 'keyup', me.onKeyUp, me);
	        }
	        me.mon(me.inputEl, 'paste', me.onPaste, me);
	        
	        me.mon(me.inputEl, 'compositionstart', me.onCompositionStart, me);
	        me.mon(me.inputEl, 'compositionend', me.onCompositionEnd, me);
	    },
	    onCompositionStart: function(){
	    	this.compositions = true;
	    },
	    onCompositionEnd: function(){
	    	this.compositions = false;
	    },
		beforeQuery : function(queryPlan) {
			var me = this;
			
			if(me.compositions) {
				// 拼音输入期间不触发
				queryPlan.cancel = true;
			}

			// Allow beforequery event to veto by returning false
			if (me.fireEvent('beforequery', queryPlan) === false) {
				queryPlan.cancel = true;
			}

			// Allow beforequery event to veto by returning setting the cancel flag
			else if (!queryPlan.cancel) {

				// If the minChars threshold has not been met, and we're not forcing an "all" query, cancel the query
				if (me.getCharLength(queryPlan.query) < me.minChars && !queryPlan.forceAll) {
					queryPlan.cancel = true;
				}
			}
			return queryPlan;
		},
		getCharLength : function(str) {
			for (var len = str.length, c = 0, i = 0; i < len; i++)
				str.charCodeAt(i) < 27 || str.charCodeAt(i) > 126 ? c += 2 : c++;
			return c;
		}
	});
	// override fieldset, toggle others when checked
	Ext.override(Ext.form.FieldSet, {
		onCheckChange : function(cmp, checked) {
			this.setExpanded(checked);
			if (checked) {
				var id = this.id, cmps = this.ownerCt.query('fieldset[name=' + this.name + ']');
				Ext.Array.each(cmps, function(c) {
					if (c.id !== id)
						c.setExpanded(false);
				});
			}
		}
	});
	// split by regexp without remove
	String.prototype._split = function(regexp) {
		if (regexp instanceof RegExp) {
			var arr = this.split(""), _arr = [], temp = '', isStr = false;
			for ( var i in arr) {
				// 字符串不拆
				if (arr[i] == "'") {
					if (temp.length > 0) {
						_arr.push("'" + temp + "'");
						isStr = false;
					} else
						isStr = true;
					temp = '';
				} else if (isStr){
					temp += arr[i];
				} else {
					if (regexp.test(arr[i])) {
						if (temp.length > 0)
							_arr.push(temp);
						_arr.push(arr[i]);
						temp = '';
					} else
						temp += arr[i];
				}
			}
			if (temp.length > 0)
				_arr.push(temp);
			return _arr;
		}
		return this.split(regexp);
	};
	// date format
	Date.prototype.format = function(format) {
		var value = this;
		var date = {
			"m+" : value.getMonth() + 1,
			"d+" : value.getDate(),
			"h+(24){0,1}" : value.getHours(),
			"(m){0,1}i+" : value.getMinutes(),
			"s+" : value.getSeconds(),
			"q+" : Math.floor((value.getMonth() + 3) / 3),
			"S+" : value.getMilliseconds()
		};
		if (/(y+)/i.test(format)) {
			format = format.replace(RegExp.$1, (value.getFullYear() + '').substr(4 - RegExp.$1.length));
		}
		for ( var k in date) {
			if (new RegExp("(" + k + ")").test(format)) {
				format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? date[k] : ("00" + date[k])
						.substr(("" + date[k]).length));
			}
		}
		return format;
	};
	// m-n的随机整数，不包括m
	Math.randomInt = function(m, n) {
		return Math.ceil(Math.random()*(n - m) + m);
	};
	// test formula with eval
	window.assertEquals = function(expected, actual) {
		var equal = false;
		if (expected instanceof Array) {
			for ( var i in expected) {
				if (expected[i] == actual)
					return;
			}
		} else {
			equal = expected == actual;
		}
		if (!equal)
			throw new Error('wrong number of arguments, expected '
					+ (expected instanceof Array ? expected.join(' or ') : expected) + ', acctually ' + actual);
	};
	// 测试用函数集
	Ext.Test = {
		randomNum: function() {
			// 测试用number
			return Number(Math.random().toFixed(6));
		},
		randomDate: function() {
			// 测试用时间
			var y = Math.randomInt(1970, 2100), m = Math.randomInt(0, 12), d = Math.randomInt(0, 30),
				h = Math.randomInt(0, 24), i = Math.randomInt(0, 60), s = Math.randomInt(0, 60);
			return new Date(y, m, d, h, i, s);
		},
		randomStr: function() {
			// 测试用字符串
			var str = '', len = 8;
			var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
			var maxPos = $chars.length;
			for (var i = 0; i < len; i++) {
				str += $chars.charAt(Math.floor(Math.random() * maxPos));
			}
			return str;
		},
		abs : function() {
			assertEquals(1, arguments.length);
			return Math.abs(arguments[0]);
		},
		ceil : function() {
			assertEquals(1, arguments.length);
			return Math.ceil(arguments[0]);
		},
		floor : function() {
			assertEquals(1, arguments.length);
			return Math.floor(arguments[0]);
		},
		round : function() {
			assertEquals([ 1, 2 ], arguments.length);
			var a = arguments[0], b = arguments[1] || 0;
			return Math.round(a * Math.pow(10, b)) / Math.pow(10, b);
		},
		nvl : function() {
			assertEquals(2, arguments.length);
			var a = arguments[0], b = arguments[1];
			return a == null ? b : a;
		},
		nvl2 : function() {
			assertEquals(3, arguments.length);
			var a = arguments[0], b = arguments[1], c = arguments[2];
			return a == null ? c : b;
		},
		lpad : function() {
			assertEquals(3, arguments.length);
			var a = arguments[0], b = Math.floor(arguments[1]), c = arguments[2];
			if (!!a) {
				a = String(a);
				c = String(c);
				var m = a.length, n = c.length, r = c.split("");
				if (m < b) {
					var i = 0, prefix = '';
					while (m < b) {
						prefix += r[i++ % n];
						m++;
					}
					a = prefix + a;
				} else if (m > b) {
					a = a.substr(0, b);
				}
			}
			return a;
		},
		rpad : function() {
			assertEquals(3, arguments.length);
			var a = arguments[0], b = Math.floor(arguments[1]), c = arguments[2];
			if (!!a) {
				a = String(a);
				c = String(c);
				var m = a.length, n = c.length, r = c.split("");
				if (m < b) {
					var i = 0;
					while (m < b) {
						a += r[i++ % n];
						m++;
					}
				} else if (m > b) {
					a = a.substr(0, b);
				}
			}
			return a;
		},
		trim : function() {
			assertEquals(1, arguments.length);
			var a = arguments[0];
			return !a ? a : String(a).trim();
		},
		to_char : function() {
			assertEquals([ 1, 2 ], arguments.length);
			var a = arguments[0], b = arguments[1];
			if(a instanceof Date) {
				return a.format(b);
			} else {
				return !a ? a : String(a);
			}
		},
		trunc : function() {
			assertEquals([ 1, 2 ], arguments.length);
			var a = arguments[0], b = arguments[1];
			if(a instanceof Date) {
				var y = a.getFullYear(), m = a.getMonth(), d = a.getDate();
				if (b) {
					b = b.toUpperCase();
					if(['Y', 'YEAR'].indexOf(b) > -1) {
						m = 0;
						d = 1;
					} else if(['M', 'MONTH'].indexOf(b) > -1) {
						d = 1;
					} else if(['D', 'DAY'].indexOf(b) > -1) {
						return new Date(new Date(y, m, d) - 86400000*a.getDay());
					}
				}
				return new Date(y, m, d);
			} else if (a instanceof Number) {
				return Math.ceil(a);
			} else {
				throw new Error('illegal argument type, trunc(' + a + ',' + b + ')');
			}
			return a;
		},
		add_months : function() {
			assertEquals(2, arguments.length);
			var a = arguments[0], b = arguments[1];
			if(a instanceof Date) {
				var y = a.getFullYear(), m = a.getMonth(), d = a.getDate();
				return new Date(y, m + b, d);
			} else {
				throw new Error('illegal argument type, add_months(' + a + ',' + b + ')');
			}
		}
	};
	Ext.eval = function(code) {
		// Ext.Test作为预定义条件
		var def = '';
		for(var i in Ext.Test) {
			if (!def) {
				def = 'var ';				
			} else {
				def += ',';
			} 
			def += i + '=' + Ext.Test[i];
		}
		def += ';';
		code = def + code;
		if (typeof execScript !== 'undefined') {
			return execScript(code);
		} else {
			return window.eval(code);
		}
	};
	//is number(Ext.isNumber doesn't work)
	var isNumber = function(n) {
		return !isNaN(parseFloat(n)) && isFinite(n);
	};
	//
	Ext.Loader.setConfig({
		enabled : true
	});//开启动态加载
	Ext.application({
		name : 'erp',//为应用程序起一个名字,相当于命名空间
		appFolder : basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'common.Search' ],
		launch : function() {
			Ext.create('erp.view.common.search.Viewport');
		}
	});
	var caller = getUrlParam('whoami');
</script>
</head>
<body>
</body>
</html>