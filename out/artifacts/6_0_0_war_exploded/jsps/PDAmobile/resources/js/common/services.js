define([ 'angular' ], function(angular) {
	'use strict';
	angular.module('common.services', [ ]).factory('SessionService', function() {
		return {
			get : function(key) {
				var storage = window.sessionStorage;
				if(storage)
					return sessionStorage.getItem(key);
				return null;
			},
			set : function(key, val) {
				var storage = window.sessionStorage;
				if(storage)
					return sessionStorage.setItem(key, val);
				return null;
			},
			unset : function(key) {
				var storage = window.sessionStorage;
				if(storage)
					return sessionStorage.removeItem(key);
				return null;
			},
			getCookie: function(key) {
				var storage = window.localStorage;
				if(storage)
					return storage.getItem(key);
				else {
					var val = document.cookie.match(new RegExp("(^| )" + key + "=([^;]*)(;|$)"));
					if (val != null) {
						return unescape(val[2]);
					}
					return null
				}
			},
			setCookie: function(key, val) {
				var storage = window.localStorage;
				if(storage)
					storage.setItem(key, val);
				else {
					var date = new Date(new Date().getTime() + 30 * 24 * 60 * 60 * 1000);
					document.cookie = key + "=" + escape(val) + ";expires=" + date.toGMTString();
				}
			},
			removeCookie: function(key) {
				var storage = window.localStorage;
				if(storage)
					storage.removeItem(key);
				else {
					var val = this.getCookie(key);
					if (val != null) {
						var date = new Date(new Date().getTime() - 1);
						document.cookie = key + "=" + val + ";expires=" + date.toGMTString()
					}
				}
			}
		};
	}).factory('BaseService', function() {
		return {
			getRootPath : function() {
				var fullPath = window.document.location.href;
				var path = window.document.location.pathname;
				var pos = fullPath.indexOf(path);
				return fullPath.substring(0, pos) + path.substring(0, path.substr(1).indexOf('/') + 1);
			}
		};
	}).factory('AuthenticationService',['$http', 'SessionService', 'BaseService', 'SerializerUtil', function($http, SessionService, BaseService, SerializerUtil) {
		var cacheSession = function() {
			SessionService.set('authenticated', true);
		};
		var uncacheSession = function() {
			SessionService.unset('authenticated');
		};
		var rootPath = BaseService.getRootPath();
		return {
			root : function() {
				return rootPath;	
			},
			login : function(user) {
				var payload = SerializerUtil.param(user);
				var config = {
					headers : {
						'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
					}
				};
				var login = $http.post(rootPath + "/pda/login.action", payload, config);
				login.success(cacheSession);
				return login;
			},
			logout : function() {
				var logout = $http.get(rootPath + "/pda/logout.action");
				logout.success(uncacheSession);
				return logout;
			},
			isAuthed : function() {
				return SessionService.get('authenticated');
			},
			getAuthentication : function() {
				var request = $http.get(rootPath + '/pda/authentication.action');
				request.success(function(data){
					if(data)
						cacheSession();
					else
						uncacheSession();
				});
				request.error(uncacheSession);
				return request;
			},
			getMasters : function(){
				var request = $http.get(rootPath +'/mobile/getAllMasters.action');
				request.success(function(data){
				});
				return request;
			}
		};
	}]).factory('SerializerUtil', function() {
		return {
			/**
			 * @description 将元素值转换为序列化的字符串表示
			 */
			param : function(obj) {
				var query = '', name, value, fullSubName, subName, subValue, innerObj, i, me = this;
				for (name in obj) {
					value = obj[name];
					if (value instanceof Array) {
						for (i = 0; i < value.length; ++i) {
							subValue = value[i];
							fullSubName = name + '[' + i + ']';
							innerObj = {};
							innerObj[fullSubName] = subValue;
							query += me.param(innerObj) + '&';
						}
					} else if (value instanceof Object) {
						for (subName in value) {
							subValue = value[subName];
							fullSubName = name + '[' + subName + ']';
							innerObj = {};
							innerObj[fullSubName] = subValue;
							query += me.param(innerObj) + '&';
						}
					} else if (value !== undefined && value !== null)
						query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
				}
				return query.length ? query.substr(0, query.length - 1) : query;
			}
		};
	});
});