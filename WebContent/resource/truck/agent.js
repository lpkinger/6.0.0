(function(global, doc) {
	if (typeof global.openDatabase !== 'function')
		return;
	// 生成uuid
	function uuid() {
		var s = [], hexDigits = "0123456789abcdef";
	    for (var i = 0; i < 36; i++) {
	        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
	    }
	    s[14] = "4";
	    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);
	    s[8] = s[13] = s[18] = s[23] = "-";
	    return s.join("");
	}
	// ajax
	var Ajax = function(config) {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			if (request.readyState == 4) {
				if (request.status == 200) {
					config.success && config.success.call(null, request.responseText);
				} else {
					config.error && config.error.call(null, request.responseText);
				}
			}
		};
		request.open("POST", config.url, true);
		request.send(config.params ? JSON.stringify(config.params) : null);
	};
	if (typeof Object.prototype.extend === 'undefined') {
		Object.prototype.extend = function(src){
			if (src) {
				var obj = this;
				Object.keys(src).forEach(function(key) {
					obj[key] = src[key];
				});				
			}
		};
	}
	// interval设置较长时，存在数据还未post而用户先关闭了浏览器的行为，采用html5 database先保存起来较合理
	var ss = global.sessionStorage, Storage = function(config) {
		var self = this;
		self.config = {db: "track_agent_db", table: "track_agent_log", flag: "track_agent_poster", debug: false}.extend(config);
		self.init();
	};
	Storage.prototype = {
			debug : function(l) {
				if (this.config.debug) {
					console.log(new Date(), l);
				}
			},
			init : function() {
				var self = this;
				self.getDB().transaction(function (tx) {
					// uid: 用户, event: 事件, desc: 行为描述, occur: 触发时间, referer: 页面,
					// args: 参数
					// 必须指定用户，一台设备可能多人在使用
					tx.executeSql("create table if not exists " + self.config.table + "(id text,uid text,event text,desc text,occur integer,referer text,args text)", []);
				});
			},
			getDB : function() {
				var self = this, db = self._db;
				!db && (self._db = global.openDatabase(self.config.db, "1.0", "track agent data", 1024 * 1024));
				return db;
			},
			push : function(log) {
				var self = this;
				self.getDB().transaction(function(tx){
					// 加uuid作为唯一标识，万一重复post了日志数据，服务器端也可避免重复保存
					tx.executeSql("insert into " + self.config.table + " values(?,?,?,?,?,?,?)", [uuid(), log.uid, log.event, log.desc, new Date().getTime(), global.location.href, log.args]);
					self.debug('push log {event: ' + log.event + ', desc: ' + log.desc + '}');
				});
			},
			pull : function(timestamp, callback) {
				var self = this;
				self.getDB().transaction(function (tx) {
					tx.executeSql("select * from " + self.config.table + " where occur<=?", [timestamp], function (ts, data) {
	                    if (data) {
	                    	var logs = [];
	                    	for (var i = 0; i < data.rows.length; i++) {
	                            logs.push(data.rows.item(i));
	                        }
	                    	self.debug('pull logs before ' + timestamp + ', size ' + logs.length);
	                    	callback.call(null, logs);
	                    }
	                });
	            });
			},
			del : function(timestamp) {
				var self = this;
				self.getDB().transaction(function (tx) {
					tx.executeSql("delete from " + self.config.table + " where<=?", [timestamp]);
					self.debug('delete logs before ' + timestamp);
	            });
			},
			bind : function(agentId) {
				var self = this, flag = self.config.flag;
				// 控制整个应用只有一个页面启用post功能
				if (!ss.getItem(flag)) {
					ss.setItem(flag, agentId);
					return true;
				}
			},
			unbind : function(agentId) {
				var self = this, flag = self.config.flag;
				(agentId == ss.getItem(flag)) && (ss.removeItem(flag));
			}
	};
	var Agent = function(config){
		var self = this;
		// enableCollect: 允许搜集数据, enablePost: 允许发送数据, interval: 发送数据间隔(分钟),
		// transferUrl: 数据中转中心, storageConfig: storage参数
		self.config = {enabled: true, debug: false, interval: 15, transferUrl: '/track/transfer'}.extend(config);
		if (self.config.enabled) {
			self.uuid = uuid();
			self.storage = new Storage({debug: self.config.debug}.extend(self.config.storageConfig));
			self.enableCollecter();
			self.enablePoster();
			self.log({event: 'load'});
			// 页面关闭前
			global.onbeforeunload = function(){
				self.log({event: 'unload'});
				self.disablePoster();
			};
		}
	};
	Agent.prototype = {
			log : function(log) {
				this.storage.push(log);
			},
			enableCollecter : function() {
				// 指标+策略
				var self = this;
				doc.onclick = function() {
					
				};
			},
			enablePoster : function() {
				// 如果使用visibilitychange事件来切换poster，在某些操作下，存在数据始终不上传的可能
				var self = this, storage = self.storage;
				global.setInterval(function(){
					if (storage.bind(self.uuid)) {
						// 方便后面删除，防止误删
						var now = new Date().getTime() - 1;
						storage.pull(now, function(logs){
							Ajax({
								url : self.config.transferUrl,
								params : logs,
								success : function(){
									storage.del(now);
								}
							});
						});
					}
				}, 60000 * self.config.interval);
			},
			disablePoster : function() {
				var self = this;
				self.storage.unbind(self.uuid);
			}
	};
	global.TrackAgent = Agent;
})(window, document);