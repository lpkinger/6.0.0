/*
控制层,
所有逻辑代码都在这里写
 */
Ext.QuickTips.init();
Ext.define('erp.controller.vendbarcode.main', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil'],
    /*    refs:[ 
    {ref: 'erpTreePanel',selector: 'erpTablePanel'}, 
    {ref: 'erpTabPanel',selector:'erpTablePanel'} 
    ], */
    stores: ['TreeStore'], //声明该控制层要用到的store
    /*   models: ['TreeModel'],//声明该控制层要用到的model
     */
    views: ['vendbarcode.main.vendbarcodeHeader', 'common.main.Bottom', 'vendbarcode.main.vendbarcodeTreePanel', 'vendbarcode.main.vendTabPanel', 'vendbarcode.main.viewPort',
         'common.main.TreeTabPanel'
    ], //声明该控制层要用到的view
    init: function() {
        var me = this;
        var timestr = '';
        this.isTureMasterFlag = true;
        me.FormUtil = Ext.create('erp.util.FormUtil');
        this.flag = true; //防止双击时tree节点重复加载
        this.naviFlag = true; //防止双击时tree节点重复加载
        //每隔8秒刷新【网络寻呼】
        Ext.defer(function() {
            me.loadPagingRelease(me.timestr,true);
        }, 1000);
        //每隔一分钟刷新[首页]---主要考虑点开首页不做任何操作
//        Ext.defer(function() {
//            me.refreshDesk();
//        }, 60000);
        this.control({
        	'vendbarcodeTreePanel': {
                itemmousedown: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                    	me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20); //防止双击时tree节点重复加载
                },
                itemclick: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                        me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20);
                },
                itemdbclick: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                        me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20);
                },
                itemmouseenter: me.showActions,
                itemmouseleave: me.hideActions,
                beforeitemmouseenter: me.showActions,
                addclick: me.handleAddClick
            },
        	'panel[id=HomePage]':{
            	activate :function(tab){
            		if(iframe_homePage){  
            		}
            	}
            },
            'menuitem[id=lock]': {
                click: function(btn) {
                    //锁定屏幕
                    me.lockPage();
                }
            },
           '#changeMaster':{
           		beforerender:function(i){
           			i.addListener('getmessage',function(){    						   
						   me.getInfoCount(me.timestr,true);
						 }    
					);    
           		}
           }
        });
    },
    showSyanavigationActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        Ext.get(icons[0]).removeCls('x-hidden');
        if (!(record.get('updateflag')==0||(record.raw &&record.raw.updateflag==0))) {
             Ext.get(icons[1]).removeCls('x-hidden');
        }
        /*if (!record.get('leaf')) {
            Ext.each(icons, function(icon) {
               Ext.get(icon).removeCls('x-hidden');
            });
        }*/
    },
    hideSyanavigationActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        Ext.each(icons, function(icon) {
            Ext.get(icon).addCls('x-hidden');
        });
    },
    loadTab: function(selModel, record) {
        var me = this;
        //text 优软商城
    	/*if(record.data.text=="优软商城"){
			me.isTureMaster();
        }*/
        me.isTureMasterFlag = true;
		if(record.raw){
	    	if((record.raw.navtype&&record.raw.navtype=='B2C')){
	    		me.isTureMaster();
	      	}
	    }
        if (record.get('leaf') || record.get('url') != null) {
            switch (record.data['showMode']) {
                case 0: //0-选项卡模式
                    me.openCard(record);
                    break;
                case 1: //1-弹出框式               
                    me.openBox(record);
                    break;
                case 2: //2-空白页
                    me.openBlank(record);
                    break;
                case 3: //3-窗口模式
                    me.openWin(record);
                    break;
                default:
                	if(record.raw){ //商城导航弹出模式选择
                		if(record.raw.navtype&&record.raw.navtype=='B2C'){
                			if(me.isTureMasterFlag){
                				switch (record.raw.showMode){
	                   			 case 0: //0-选项卡模式
	   			                    me.openCard(record);
	   			                    break;
	   			                case 1: //1-弹出框式               
	   			                    me.openBox(record);
	   			                    break;
	   			                case 2: //2-空白页
	   			                    me.openBlank(record);
	   			                    break;
	   			                case 3: //3-窗口模式
	   			                    me.openWin(record);
	   			                    break;
	   			                case 4:	//4-全屏幕窗口模式
	   								me.openFrameWin(record);
	   								break;
	   							 default:
	   								 me.openCard(record);
	   							  	 break;
	                   			}
                			}
                		}else me.openCard(record);
                	}else me.openCard(record);
                    break;
            }
            var w = Ext.getCmp("content-panel").items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow;
            me.setCommonUse(record.get('url'),record.get('caller'));
            me.flag = true;
        }
        if (!record.get('leaf') && !(record.raw && record.raw.queryMode=='CLOUD')) {
            if (record.isExpanded() && record.childNodes.length > 0) { //是根节点，且已展开
                record.collapse(true, true); //收拢
                me.flag = true;
            } else { //未展开
                //看是否加载了其children
                if (record.childNodes.length == 0) {
                    //从后台加载
                    var tree = Ext.getCmp('tree-panel');
                    var condition = tree.baseCondition;
                    tree.setLoading(true, tree.body);
                    Ext.Ajax.request({ //拿到tree数据
                        url: basePath + 'common/lazyTree.action',
                        params: {
                            parentId: record.data['id'],
                            condition: condition
                        },
                        callback: function(options, success, response) {
                            tree.setLoading(false);
                            var res = new Ext.decode(response.responseText);
                            if (res.tree) {
                                if (!record.get('level')) {
                                    record.set('level', 0);
                                }
                                Ext.each(res.tree, function(n) {
                                    if (n.showMode == 2) { //openBlank
                                        n.text = "<a href='" + basePath + me.parseUrl(n.url) + "' target='_blank'>" + n.text + "</a>";
                                    }
                                    if (!n.leaf) {
                                        n.level = record.get('level') + 1;
                                        n.iconCls = 'x-tree-icon-level-' + n.level;
                                    }
                                });
                                record.appendChild(res.tree);
                                record.expand(false, true); //展开
                                me.flag = true;
                            } else if (res.exceptionInfo) {
                                showError(res.exceptionInfo);
                                me.flag = true;
                            }
                        }
                    });
                } else {
                		record.expand(false, true); //展开
                    me.flag = true;
                }
            }
        }
        if(record.raw){
        	if((record.raw.navtype&&record.raw.navtype=='B2C')&&(!me.isTureMasterFlag)){
        		Ext.defer(function(){
        			record.collapse(true); //收拢
        		},500);
        	}
        }
    },
    
    openTab: function(panel, id, url) {
        var o = (typeof panel == "string" ? panel : id || panel.id);
        var main = Ext.getCmp("content-panel");
        var tab = main.getComponent(o);
        if (tab) {
            main.setActiveTab(tab);
        } else if (typeof panel != "string") {
            panel.id = o;
            var p = main.add(panel);
            main.setActiveTab(p);
        }
    },
    getMyNewEmails: function() {
        Ext.Ajax.request({
            url: basePath + "oa/mail/getNewMail.action",
            method: 'post',
            callback: function(options, success, response) {
                var res = new Ext.decode(response.responseText);
                if (res.exceptionInfo) {
                    showError(res.exceptionInfo);
                    return;
                }
            }
        });
    },
    setCommonUse:function(url,caller){
		Ext.Ajax.request({
			url : basePath + 'common/setCommonUse.action',
			params: {
				url:url,
				count:15,
				caller:caller
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exception || res.exceptionInfo){
					showError(res.exceptionInfo);
					return;
				}
			}
		});
	},
    parseUrl: function(url) {
        var id = url.substring(url.lastIndexOf('?') + 1); //将作为新tab的id
        if (id == null) {
            id = url.substring(0, url.lastIndexOf('.'));
        }
        if (contains(url, 'session:em_uu', true)) { //对url中session值的处理
            url = url.replace(/session:em_uu/g, em_uu);
        }
        if (contains(url, 'session:em_code', true)) { //对url中em_code值的处理
            url = url.replace(/session:em_code/g, "'" + em_code + "'");
        }
        if (contains(url, 'sysdate', true)) { //对url中系统时间sysdate的处理
            url = url.replace(/sysdate/g, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
        }
        if (contains(url, 'session:em_name', true)) {
            url = url.replace(/session:em_name/g, "'" + em_name + "'");
        }
        if (contains(url, 'session:em_type', true)) {
            url = url.replace(/session:em_type/g, "'" + em_type + "'");
        }
        if (contains(url, 'session:em_id', true)) {
            url = url.replace(/session:em_id/g,em_id);
        }
        if (contains(url, 'session:em_depart', true)) {
            url = url.replace(/session:em_depart/g,em_id);
        }
        if (contains(url, 'session:em_defaulthsid', true)) {
            url = url.replace(/session:em_defaulthsid/g,em_defaulthsid);
        }
        return url;
    },
    openCard: function(record) {
        var me = this;
        var panel = Ext.getCmp(record.get('id'));
        if (!panel) {
        	var url="";
        	/**是否取云端导航配置*/
        	if(record.parentNode &&  record.parentNode.raw && record.parentNode.raw.queryMode=='CLOUD'){
        		url=record.raw.url;
        		if(url) url+=(url.indexOf("?")>0?'&_config=CLOUD':'?_config=CLOUD');
        	}else if(record.parentNode &&  record.parentNode.raw && record.parentNode.raw.queryMode=='SYSSETTING'){
        		url=record.raw.url;
        	}else url=record.data['url'];
            url = me.parseUrl(url); //解析url里的特殊描述
            panel = {
                title: record.get('text').length > 5 ? (record.get('text').substring(0, 5) + '..') : record.get('text'),
                tag: 'iframe',
                tabConfig: {
                    tooltip: record.get('qtip')
                },
                border: false,
                frame: false,
                layout: 'fit',
                iconCls: record.data.iconCls,
                html: '<iframe id="iframe_' + id + '" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
                closable: true,
                listeners: {
                    close: function() {
                        var main = Ext.getCmp("content-panel");
                        main.setActiveTab(Ext.getCmp("HomePage"));
                    }
                }
            };
            if (record.get('leaf')) {
            this.openTab(panel, record.get('id'), url);}
        } else {
            var main = Ext.getCmp("content-panel");
            main.setActiveTab(panel);
        }
    },
    openBox: function(record) {
        window.open(basePath + this.parseUrl(record.data['url']), record.get('qtip'), 'width=' + (window.screen.width - 10) +
            ',height=' + (window.screen.height * 0.87) + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
    },
    openBlank: function(record) {
        //window.open(basePath + this.parseUrl(record.data['url']));
    },
    openWin: function(record) {
        if (Ext.getCmp('twin_' + record.data['id'])) {
            Ext.getCmp('twin_' + record.data['id']).show();
        } else {
            new Ext.window.Window({
                id: 'twin_' + record.data['id'],
                title: record.get('qtip').length > 5 ? (record.get('qtip').substring(0, 5) + '..') : record.get('qtip'),
                height: "100%",
                width: "80%",
                maximizable: true,
                layout: 'anchor',
                items: [{
                    tag: 'iframe',
                    frame: true,
                    anchor: '100% 100%',
                    layout: 'fit',
                    html: '<iframe id="iframe_twin" src="' + basePath + this.parseUrl(record.data['url']) + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                }]
            }).show();
        }
    },
    lockPage: function() {
        var me = this;
        Ext.Ajax.request({
            url: basePath + "common/logout.action",
            method: 'post',
            callback: function(options, success, response) {
                var res = Ext.decode(response.responseText);
                if (res.success) {
                    //弹出解锁框
                    me.showLock();
                }
            }
        });
    },
    /**
     * 显示锁屏window
     */
    showLock: function() {
        var me = this;
        var panel = Ext.create('Ext.window.Window', {
            id: 'lock-win',
            frame: true,
            closable: false,
            modal: true,
            autoShow: true,
            title: '<div style="height:25;padding-top:5px;color:blue;background: #E0EEEE url(' + basePath + 'resource/ext/resources/themes/images/default/grid/grid-blue-hd.gif) repeat center center">&nbsp;！您的屏幕已锁定</div>',
            bodyStyle: 'background: #E0EEEE',
            width: 360,
            height: 260,
            renderTo: Ext.getBody(),
            items: [{
                xtype: 'displayfield',
                height: 130,
                labelWidth: 128,
                labelSeparator: '',
                fieldStyle: 'color:#7D9EC0;font-size:15px;font-family:隶书;',
                fieldLabel: '<img src="' + basePath + 'resource/images/screens/locked.png" style="background: #E0EEEE;">',
                value: '如需解锁，请输入您的密码'
            }, {
                xtype: 'hidden',
                name: 'username',
                id: 'username',
                value: em_code
            }, {
                xtype: 'form',
                bodyStyle: 'background: #E0EEEE',
                layout: 'column',
                items: [{
                    xtype: 'textfield',
                    labelSeparator: '',
                    columnWidth: 0.8,
                    fieldLabel: '<img src="' + basePath + 'resource/images/screens/key.png" style="background: #E0EEEE;padding-left:15px;">',
                    labelWidth: 40,
                    fieldCls: 'x-form-field-cir',
                    id: 'password',
                    name: 'password',
                    inputType: 'password'
                }, {
                    xtype: 'button',
                    columnWidth: 0.2,
                    cls: 'x-btn-blue',
                    text: '解锁',
                    handler: function() {
                        me.removeLock();
                    }
                }]
            }]
        });
        panel.el.slideIn('b', {
            duration: 1000
        });
    },
    removeLock: function() {
        var win = Ext.getCmp('lock-win');
        if (win && win.down('#password').value != null) {
            Ext.Ajax.request({
                url: basePath + "common/login.action",
                params: {
                    username: win.down('#username').value,
                    password: win.down('#password').value,
                    language: language
                },
                method: 'post',
                callback: function(options, success, response) {
                    var res = Ext.decode(response.responseText);
                    if (res.success) {
                        //弹出解锁框
                        win.close();
                    } else {
                        if (res.reason) {
                            alert(res.reason);
                            win.down('#password').setValue('');
                            win.down('#password').focus();
                        }
                    }
                }
            });
        }
    },
    /**
     * 右下角小消息提示
     * @param title 标题
     * @param fromId 发送人Id
     * @param from 发送人
     * @param date 日期
     * @param context 正文
     * @param url 消息链接
     * @param msgId 消息ID
     */
    showMsgTip: function(title, prId, fromId, from, date, context, url, msgId, master) {
        var me = this;
        var panel = Ext.getCmp('msg-win-' + prId);
        if (!panel) {
            panel = Ext.create('erp.view.core.window.MsgTip', {
                title: title,
                from: from,
                date: date,
                url: url,
                msgId: msgId,
                prId: prId,
                height: 120, //提示信息显示不全
                context: context,
                listeners: {
                    close: function() {
                        me.updatePagingStatus(msgId, 1, master);
                    },
                    check: function() {
                        me.showDialogBox(msgId, fromId, from, date, context);
                    },
                    reply: function() {
                        me.showDialogBox(msgId, fromId, from, date, context);
                    }
                }
            });
        }
    },
    transImages: function(msg) {
        msg = msg.toString();
        var faces = msg.match(/&f\d+;/g);
        Ext.each(faces, function(f) { //表情
            msg = msg.replace(f, '<img src="' + basePath + 'resource/images/face/' + f.substr(2).replace(';', '') + '.gif">');
        });
        var images = msg.match(/&img\d+;/g);
        Ext.each(images, function(m) { //图片
            msg = msg.replace(m, '');
        });
        return msg;
    },
    /**
     * 对话框
     * @param id 消息的主键值
     * @param otherId 对方人员ID
     * @param other 对方人名
     * @param date 时间
     * @param context 对话内容 
     */
    showDialogBox: function(id, otherId, other, date, context) {
        var me = this;
        var panel = Ext.getCmp('dialog-win-' + otherId);
        if (!panel) {
            panel = Ext.create('erp.view.core.window.DialogBox', {
                other: other,
                otherId: otherId
            });
        }
        if (!Ext.isEmpty(id)) {
            panel.insertDialogItem(other, date, context);
            if (Ext.getCmp('dialog-min-' + otherId)) {
                Ext.getCmp('dialog-min-' + otherId).setText("<font color=red>有新消息...</font>");
            } else {
                me.updatePagingStatus(id, 1);
            }
        }
    },
    /**
     * 循环刷新寻呼信息
     * @param cycletime 间隔时间 {快速4000(聊天过程中)、中等8000(普通模式)、慢速15000(session中断等异常情况下)}
     */
    //20170302 修改了刷新时间为20秒一次。
    loadPagingRelease: function(timestr,sync) {
        var me = this;
        me.cycletime = me.cycletime || 20000;
        if (!Ext.getCmp('lock-win')) {
            try {
                //me.getPagingRelease();
            	me.getInfoCount(me.timestr,sync);
                Ext.getCmp('process-lazy').setText('');
            } catch (e) {
                //需要try catch一下，不然，循环会因出现的异常而中断。网络中断后，如果不刷新主页，而是直接重新登录的话，就不会继续循环刷新寻呼
                me._showerr(e);
            }
        }
        setTimeout(function() {
            me.loadPagingRelease(me.timestr,true);
        }, me.cycletime);
    },
    _showerr: function(e) {
        var me = this;
        if (e.code == 101 || e.message == 'NETWORK_ERR' || e.message == 'NETWORK_LAZY' || e.message == 'NETWORK_LOCK' || e.message == 'NETWORK_KICK') { //NETWORK_ERR
        	me.cycletime = 20000;
            if (e.message == 'NETWORK_ERR') {
                showVendLoginDiv(true);
                Ext.getCmp('process-lazy').setText('服务器连接中断，服务器可能已关闭或在重启，尝试连接中...');
            } else if (e.message == 'NETWORK_LAZY') {
                Ext.getCmp('process-lazy').setText('请求超时8000ms，服务器负荷过大或网络延迟，请暂缓操作，尝试恢复中...');
            } 
        }
    },
    
  isMinStatus:function() { 
	var isMin = false;
	if (window.outerWidth != undefined) { 
		isMin = window.outerWidth <= 160 && window.outerHeight <= 28; 
	} 
	else { 
		isMin = window.screenTop < -30000 && window.screenLeft < -30000; 
	} 
	return isMin; 
},  
getInfoCount:function(timestr,sync){
    var me = this,
         t1 = new Date().getTime();
    Ext.Ajax.request({
        url: basePath + 'vendbarcode/getInfoCount.action',
        method: 'get',
        params:{
        	timestr:timestr
        },
        async:true,
        timeout: 8000,
        callback: function(options, success, response) {
            var e = null;
            if (success == false) {
                var lazy = new Date().getTime() - t1;
                console.log('延迟'+lazy);
                if (lazy > 7500) { // 表示超时引起
                    e = new Error("NETWORK_LAZY");
                } else { // 表示服务器连接中断引起
                    e = new Error("NETWORK_ERR");
                }
            } else {
                me.cycletime = 20000;
            }
            var localJson = new Ext.decode(response.responseText, true);
            if (localJson.exceptionInfo) {
                var info = localJson.exceptionInfo;
                if (info == 'ERR_NETWORK_SESSIONOUT') {
                    e = new Error("NETWORK_ERR");
                }else {
                    showMessage('警告', info);
                }
            }else if(localJson.emp){
            	e = new Error("NETWORK_ERR");
            }
            if (e != null) {
                me._showerr(e);
                return;
            }      
        }
    });
},
    showActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        if (record.get('addurl') || (record.raw && record.raw.addurl)) {
            Ext.each(icons, function(icon) {
                Ext.get(icon).removeCls('x-hidden');
            });
        }
    },
    hideActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        Ext.each(icons, function(icon) {
            Ext.get(icon).addCls('x-hidden');
        });
    },
    handleAddClick: function(view, rowIndex, colIndex, column, e) {
    	var me=this;
        var record = view.getRecord(view.findTargetByEvent(e)),
            title = record.get('text');
        if (record.get('addurl') ||  record.raw.addurl) {
            openTable(title, record.get('addurl')|| record.raw.addurl, record.get('caller'),true);
            me.setCommonUse(record.get('addurl'),record.get('caller'));
        }
    }
})
  
  