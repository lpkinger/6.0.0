/**
 * 网络寻呼--聊天框
 * (在系统主页工作台下的通讯录，选择任意人员即可打开聊天框)
 * 支持在线、离线消息、图片、附件
 * 支持查看、清除消息历史
 * 需传递参数other、otherId、[date]、[context]
 */
Ext.define('erp.view.core.window.DialogBox', {
	extend: 'Ext.window.Window',
	alias: 'widget.dialogbox',
	frame: true,
	closable: false,
	autoShow: true,
	bodyStyle: 'background: #E0EEEE',
	width: 550,
	height: 460,
	renderTo: Ext.getBody(),
	layout: 'border',
	initComponent: function() {
		this.id = 'dialog-win-' + this.otherId;
		this.title = '<div style="height:25;padding-top:5px;color:#FF6A6A;background: #E0EEEE url(' + 
				basePath + 'resource/ext/resources/themes/images/default/grid/grid-blue-hd.gif) repeat center center">&nbsp;您与 【' + this.other + '】 的会话:</div>';
		this.items = [{
			region: 'center',
			items: [{
				xtype: 'panel',
				id: 'log' + this.otherId,
				height: 255,
				width: 550,
				autoScroll: true,
				bodyStyle: 'background: #E0EEEE;'
			}, this.createDialogForm()]
		},{
			xtype: 'panel',
			region: 'east',
			height: 450,
			width: 320,
			hidden: true,
			id: 'history' + this.otherId,
			autoScroll: true,
			bodyStyle: 'background: #E0EEEE',
			bbar: this.getPagingToolbar(),
			tbar: this.getQueryToolbar()
		}];
    	this.callParent(arguments);
    	this.updatePosition();
    	this.baseCondition = "(pr_releaserid=" + em_uu + " AND prd_recipientid=" + this.otherId + ") OR (pr_releaserid=" + 
			this.otherId + " AND prd_recipientid=" + em_uu + ")";
    },
    tools: [{
		type: 'minimize',
		handler: function(){//最小化
			var win = arguments[2].ownerCt;
			var b = Ext.getCmp('bottom') || parent.Ext.getCmp('bottom');
			if(b){
				b.insert(1, {
					id: 'dialog-min-' + win.otherId,
					text: win.other,
					tooltip: win.context,
					tab: win,
					width: 150,
					style: {
						background: '#E0EEEE'
					},
					handler: function(btn){
						btn.tab.show();
			    		btn.destroy();
					}
				});
				win.hide();
			}
		}
	},{
		type: 'close',
		handler: function(){
			var win = arguments[2].ownerCt;
			win.close();
		}
	}],
	/**
	 * 聊天输入框
	 */
    createDialogForm: function(){
    	var me = this;
    	me.dialogForm = Ext.create('Ext.form.Panel', {
    		width: 550,   	
			items: [{
				xtype: 'htmleditor',
		        enableColors: false,
		        enableAlignments: false,
		        enableFont: false,
		        enableFontSize: false,
		        enableFormat: false,
		        enableLinks: false,
		        enableLists: false,
		        enableSourceEdit: false,
				name: 'msg',
				frame: false,
				height: 115,
				width: 550,
				fieldStyle: 'border-bottom: none;',					
				 fixKeys: function() { // load time branching for fastest keydown performance
				        if (Ext.isIE) {
				            return function(e){
				                var me = this,
				                    k = e.getKey(),
				                    doc = me.getDoc(),
				                    range, target;
				                if (k === e.TAB) {
				                    e.stopEvent();
				                    range = doc.selection.createRange();
				                    if(range){
				                        range.collapse(true);
				                        range.pasteHTML('&nbsp;&nbsp;&nbsp;&nbsp;');
				                        me.deferFocus();
				                    }
				                }
				                else if (k === e.ENTER) {
				                    range = doc.selection.createRange();
				                    if (range) {
				                        target = range.parentElement();
				                        if(!target || target.tagName.toLowerCase() !== 'li'){
				                            e.stopEvent();
				                            range.pasteHTML('<br />');
				                            range.collapse(false);
				                            range.select();
				                        }
				                    }
				                }
				            };
				        }

				        if (Ext.isOpera) {
				            return function(e){
				                var me = this;
				                if (e.getKey() === e.TAB) {
				                    e.stopEvent();
				                    me.win.focus();
				                    me.execCmd('InsertHTML','&nbsp;&nbsp;&nbsp;&nbsp;');
				                    me.deferFocus();
				                }
				            };
				        }

				        if (Ext.isWebKit) {
				            return function(e){
				                var me = this,
				                    k = e.getKey();
				                if (k === e.TAB) {
				                    e.stopEvent();
				                    me.execCmd('InsertText','\t');
				                    me.deferFocus();
				                }
				                else if (k === e.ENTER) {
				                    e.stopEvent();
				                    me.execCmd('InsertHtml','<br /><br />');
				                    me.deferFocus();
				                }
				            };
				        }

				        return null; // not needed, so null
				    }(),
				    initEditor : function(){
				        //Destroying the component during/before initEditor can cause issues.
				        try {
				            var me = this,
				                dbody = me.getEditorBody(),
				                ss = me.textareaEl.getStyles('font-size', 'font-family', 'background-image', 'background-repeat', 'background-color', 'color'),
				                doc,
				                fn;

				            ss['background-attachment'] = 'fixed'; // w3c
				            dbody.bgProperties = 'fixed'; // ie

				            Ext.DomHelper.applyStyles(dbody, ss);

				            doc = me.getDoc();

				            if (doc) {
				                try {
				                    Ext.EventManager.removeAll(doc);
				                } catch(e) {}
				            }

				            /*
				             * We need to use createDelegate here, because when using buffer, the delayed task is added
				             * as a property to the function. When the listener is removed, the task is deleted from the function.
				             * Since onEditorEvent is shared on the prototype, if we have multiple html editors, the first time one of the editors
				             * is destroyed, it causes the fn to be deleted from the prototype, which causes errors. Essentially, we're just anonymizing the function.
				             */
				            fn = Ext.Function.bind(me.onEditorEvent, me);
				            Ext.EventManager.on(doc, {
				                mousedown: fn,
				                dblclick: fn,
				                click: fn,
				                keyup: fn,
				                buffer:100
				            });

				            // These events need to be relayed from the inner document (where they stop
				            // bubbling) up to the outer document. This has to be done at the DOM level so
				            // the event reaches listeners on elements like the document body. The effected
				            // mechanisms that depend on this bubbling behavior are listed to the right
				            // of the event.
				            fn = me.onRelayedEvent;
				            Ext.EventManager.on(doc, {
				                mousedown: fn, // menu dismisal (MenuManager) and Window onMouseDown (toFront)
				                mousemove: fn, // window resize drag detection
				                mouseup: fn,   // window resize termination
				                click: fn,     // not sure, but just to be safe
				                dblclick: fn,  // not sure again  
				                keydown:this.onEditorKeyDownEvent, 
				                scope: me
				            });

				            if (Ext.isGecko) {
				                Ext.EventManager.on(doc, 'keypress', me.applyCommand, me);
				            }
				            if (me.fixKeys) {
				                Ext.EventManager.on(doc, 'keydown', me.fixKeys, me);
				            }
				            Ext.EventManager.on(window, 'unload', me.beforeDestroy, me);
				            doc.editorInitialized = true;
				            me.initialized = true;
				            me.pushValue();
				            me.setReadOnly(me.readOnly);
				            me.fireEvent('initialize', me);
				        } catch(ex) {
				            // ignore (why?)
				        }
				    },
				    onEditorKeyDownEvent : function(e){  
				        //this.updateToolbar();  
				        this.fireEvent("keydown", this, e);  
				    },  
				initComponent : function(){
			        var me = this;
			        me.addEvents(
			            'initialize',
			            'activate',
			            'beforesync',
			            'beforepush',
			            'sync',			          
			            'push',
			            'editmodechange',
			            'keydown'
			        );
			        me.callParent(arguments);
			        me.initLabelable();
			        me.initField();
			    },
				listeners: {
			    	keydown:function(component,event){
			    		if(event.keyCode==13||event.ctrlKey&&event.keyCode==13){
			    			Ext.getCmp('msg-post').handler();
			    		}
			    	}
			    },
			}],
			buttonAlign: 'right',			
			buttons: [{
				text: '寻呼记录',
				cls: 'x-btn-blue',
				height: 23,
				width: 80,
				handler: function(){
					if(Ext.getCmp('history' + me.otherId).hidden){
						me.setWidth(870);
						Ext.getCmp('history' + me.otherId).show();
						me.getCount();
					} else {
						me.setWidth(550);
						Ext.getCmp('history' + me.otherId).hide();
					}
				}
			},{
				text: '关&nbsp;闭',
				cls: 'x-btn-blue',
				height: 23,
				width: 80,
				handler: function(btn){
					me.close();
				}
			},{
				text: '发&nbsp;送',
				cls: 'x-btn-blue',
				height: 23,
				width: 80,
				id:'msg-post',
				handler: function(btn){
					me.post();
				}
			}],
			tbar: {
				xtype: 'toolbar',
				height: 25,
				items: [{
					iconCls: 'x-tree-icon-happy',
					cls: 'x-btn-blue',
					text: '选择表情',
					handler: function(btn){
						me.showFace(btn);
					}
				},me.picture(),{
					iconCls: 'x-button-icon-up',
					text: '发送附件',
					cls: 'x-btn-blue'
				},'->',{
					text: '常用语',
					cls: 'x-btn-blue',
					menu: [{
						text: '谢谢！'
					},{
						text: '哦，好的。'
					},{
						text: '嗯，知道了。'
					},{
						text: '您好！我现在正忙，一会儿回复您。'
					}]
				}]
			}
    	});
    	return me.dialogForm;
    },
    /**
     * 插入聊天记录
     */
    insertDialogItem: function(name, date, context, cmp){
    	context = this.transImages(context);
    	cmp = cmp || this.down('#log' + this.otherId);
    	cmp.add({
			xtype: 'displayfield',
			height: 'auto',
			fieldLabel: (name == null || name == em_name) ? '<font color=green style="font-weight:bold;font-family:宋体;">我:</font>' + 
					"<font color=green style='margin-left:5px;'>(" + date + ")</font>" : 
					'<font color=blue style="font-weight:bold;font-family:宋体;">' + name + ':</font>' + 
				"<font color=blue style='margin-left:5px;'>(" + date + ")</font>",
			labelWidth: 300,
			labelSeparator: '',
			fieldStyle: 'color:green'
		});
    	cmp.add({
			xtype: 'displayfield',
			labelSeparator: '',
			height: 'auto',
			fieldStyle: 'padding-left:30px;color:black;',
			value: context
		});
    	cmp.setActive( true,cmp.items.items[cmp.items.items.length-1]);
    },
    /**
     * 显示表情picker
     */
	showFace: function(btn){
		var picker = Ext.getCmp(btn.id + '-picker');
		if(picker){
			if(picker.hidden){
				picker.show();
			} else {
				picker.hide();
			}
		} else {
			picker = Ext.create('erp.view.core.picker.Face', {
				id: btn.id + '-picker',
	    	    floating: true,
	    	    ownerCt: btn, 
	    	    hidden: true,
	    	    renderTo: Ext.getBody(),
	    	    listeners: {
	    	    	scope:this,
	    	    	select: function(picker, face) {
	    	        	var msg = btn.ownerCt.ownerCt.down('htmleditor[name=msg]');
	    	            var element = document.createElement("img");
                        element.src = picker.facepath + face.substr(2).replace(';', '') + picker.format;
                        element.title = face;
                        msg.getEl().dom.getElementsByTagName('iframe')[0].contentWindow.document.body.appendChild(element);
	    	            picker.hide();
	    	        }
	    	    }
	    	});
	    	picker.alignTo(btn.getEl(), 'tl-bl?');
	    	picker.show(btn.getEl());
		}
	},
	post: function(){
		var me = this;
		var form = me.dialogForm;
		var area = form.down('htmleditor[name=msg]');
		if(area.getValue() != null && area.getValue() != ''){
            var imgs = area.getEl().dom.getElementsByTagName('iframe')[0].contentWindow.document.body.getElementsByTagName('img');
            var value = area.value;
            me.insertDialogItem(null, Ext.Date.format(new Date(), 'Y-m-d H:i:s'), me.transImages(value));
            Ext.each(imgs, function(i){
            	value = value.replace(i.outerHTML, i.title);
            });
			me.sendDialog(value);
			area.setValue('');
		}
	},
	/**
	 * 图片信息转化
	 * (纯html格式会加大数据量。这里将图片、附件等转化成特殊的描述，可以简化信息量)
	 * 分为表情&f;,用户上传图片&img;
	 */
	transImages: function(msg){
		msg = msg.toString();
		var faces = msg.match(/&f\d+;/g);
		Ext.each(faces, function(f){//表情
			msg = msg.replace(f, '<img src="' + basePath + 'resource/images/face/' + f.substr(2).replace(';', '') + '.gif">');
		});
		var images = msg.match(/&img\d+;/g);
		Ext.each(images, function(m){//图片
			var id = m.substr(4).replace(';', '');
			Ext.Ajax.request({
	        	url : basePath + 'common/getFilePaths.action',
	        	async: false,
	        	params: {
	        		id:  id
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.files && res.files.length > 0){
	        			msg = msg.replace("&img" + id + ";", '<img src="' + res.files[0].fp_path + '">');
	        		}
	        	}
	        });
		});
		return msg;
	},
	/**
     * 发送寻呼
     */
    sendDialog: function(context){
    	var me = this;
    	Ext.Ajax.request({
	   		url : basePath + 'oa/info/sendPagingRelease.action',
	   		async: false,
	   		params: {
	   			formStore: Ext.encode({
	   				prd_recipient: me.other,
		   			prd_recipientid: me.otherId,
		   			pr_context: context
	   			})
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				
	   			}
	   		}
		});
    },
    /**
     * window显示位置
     */
    updatePosition: function(){
    	var count = Ext.ComponentQuery.query('dialogbox').length;//多个聊天框时，错位显示
    	var x = (screen.width - 500)/2;
    	var y = (screen.height - 500)/2;
    	this.setPosition(x - count*30, y - count*30);
    	this.show();
    },
    page: 1,
    pageSize: 10,
    getPagingToolbar: function(){
    	var box = this,
    		pageSize = box.pageSize,
    		dataCount = box.dataCount;
    	var bar = Ext.create('Ext.toolbar.Paging', {
    		id: 'pagingtoolbar' + box.otherId,
    		updateInfo : function(){
    			var page=this.child('#inputItem').getValue();
    			var me = this,
	 	    	 	displayItem = me.child('#displayItem'),
	 	    	 	pageData = me.getPageData();
                   	pageData.fromRecord = (page-1)*pageSize+1;
	    			pageData.toRecord = page*pageSize > dataCount ? dataCount : page*pageSize;
	    			pageData.total = dataCount;
	    		var msg;
	                if (displayItem) {
	                    if (box.dataCount === 0) {
	                        msg = me.emptyMsg;
	                    } else {
	                        msg = Ext.String.format(
	                            me.displayMsg,
	                            pageData.fromRecord,
	                            pageData.toRecord,
	                            pageData.total
	                        );
	                    }
	                    displayItem.setText(msg);
	                    me.doComponentLayout();
	                }
	            },
	            getPageData : function(){
	            	var totalCount = box.dataCount;
		        	return {
		        		total : totalCount,
		        		currentPage : box.page,
		        		pageCount: Math.ceil(box.dataCount / box.pageSize),
		        		fromRecord: ((box.page - 1) * box.pageSize) + 1,
		        		toRecord: Math.min(box.page * box.pageSize, totalCount)
		        	};
		        },
		        doRefresh:function(){
			    	this.moveFirst();
			    },
		        onPagingKeyDown : function(field, e){
		            var me = this,
		                k = e.getKey(),
		                pageData = me.getPageData(),
		                increment = e.shiftKey ? 10 : 1,
		                pageNum = 0;

		            if (k == e.RETURN) {
		                e.stopEvent();
		                pageNum = me.readPageFromInput(pageData);
		                if (pageNum !== false) {
		                    pageNum = Math.min(Math.max(1, pageNum), pageData.pageCount);
		                    me.child('#inputItem').setValue(pageNum);
		                    if(me.fireEvent('beforechange', me, pageNum) !== false){
		                    	box.page = pageNum;
		                    	box.getHistoryStore(box.page, box.pageSize);
		                    }
		                    
		                }
		            } else if (k == e.HOME || k == e.END) {
		                e.stopEvent();
		                pageNum = k == e.HOME ? 1 : pageData.pageCount;
		                field.setValue(pageNum);
		            } else if (k == e.UP || k == e.PAGEUP || k == e.DOWN || k == e.PAGEDOWN) {
		                e.stopEvent();
		                pageNum = me.readPageFromInput(pageData);
		                if (pageNum) {
		                    if (k == e.DOWN || k == e.PAGEDOWN) {
		                        increment *= -1;
		                    }
		                    pageNum += increment;
		                    if (pageNum >= 1 && pageNum <= pageData.pages) {
		                        field.setValue(pageNum);
		                    }
		                }
		            }
		            me.updateInfo();
		            me.resetTool(value);
		        }, 
		        moveFirst : function(){
	            	var me = this;
	                me.child('#inputItem').setValue(1);
	                value = 1;
	            	box.page = value;
	            	box.getHistoryStore(value, pageSize);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            movePrevious : function(){
	                var me = this;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue() - 1);
	                value = me.child('#inputItem').getValue();
	                box.page = value;
	            	box.getHistoryStore(value, pageSize);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            moveNext : function(){
	                var me = this,
	                last = me.getPageData().pageCount;
	                total = last;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue() + 1);
	                value = me.child('#inputItem').getValue();
	                box.page = value;
	            	box.getHistoryStore(value, pageSize);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            moveLast : function(){
	                var me = this,
	                last = me.getPageData().pageCount;
	                total = last;
	                me.child('#inputItem').setValue(last);
	                value = me.child('#inputItem').getValue();
	            	box.page = value;
	            	box.getHistoryStore(value, pageSize);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            onLoad : function() {
					var e = this, d, b, c, a;
					if (!e.rendered) {
						return
					}
					d = e.getPageData();
					b = d.currentPage;
					c = Math.ceil(box.dataCount / pageSize);
					a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
					e.child("#afterTextItem").setText(a);
					e.child("#inputItem").setValue(b);
					e.child("#first").setDisabled(b === 1);
					e.child("#prev").setDisabled(b === 1);
					e.child("#next").setDisabled(b === c || c===1);//
					e.child("#last").setDisabled(b === c || c===1);
					e.child("#refresh").enable();
					e.updateInfo();
					e.fireEvent("change", e, d);
				},
				resetTool: function(value){
					this.child('#last').setDisabled(value == box.dataCount);
				    this.child('#next').setDisabled(value == total || value == 1);
				    this.child('#first').setDisabled(value <= 1);
				    this.child('#prev').setDisabled(value <= 1);
				}
    	});
    	return bar;
    },
    getQueryToolbar: function(){
    	var me = this;
    	return {
    		xtype: 'toolbar',
    		items: [{
    			iconCls: 'x-button-icon-query',
    			width: 18,
    			style: {
    				marginBottom: '5px',
    				border: 'none'
    			},
    			tooltip: '查询',
    			handler: function(){
    				var value = me.down('condatefield[name=pr_date]').value;
    				if(!Ext.isEmpty(value)){
    					me.page = 1;
    					me.filterCondition = "(pr_date " + value + ")";
    			    	me.getCount();
    				}
    			}
    		}, Ext.create('erp.view.core.form.ConDateField', {
    			name: 'pr_date',
    			width: 295
    		})]
    	};
    },
	getHistoryStore: function(page, pageSize){
		var me = this;
		me.down('#history' + me.otherId).removeAll();
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/datalist.action',
        	params: {
        		caller: 'PagingRelease',
        		condition:  me.filterCondition ? me.filterCondition + ' AND (' + me.baseCondition + ')': me.baseCondition, 
        		page: page,
        		pageSize: pageSize
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];//一定要去掉多余逗号，ie对此很敏感
        		data = Ext.Array.sort(data, function(a, b){//按时间升序排序
        			return a.pr_date > b.pr_date;
        		});
        		Ext.each(data, function(d){
        			me.insertDialogItem(d.pr_releaser, Ext.Date.format(Ext.Date.parse(d.pr_date, 'Y-m-d H:i:s'), 'Y-m-d H:i:s'), 
        					d.pr_context, me.down('#history' + me.otherId));
        		});
        		//修改pagingtoolbar信息
        		Ext.getCmp('pagingtoolbar' + me.otherId).onLoad();
        	}
        });
	},
	getCount: function(){
		var me = this;
		Ext.Ajax.request({
        	url : basePath + '/common/datalistCount.action',
        	params: {
        		_noc:1,
        		caller: 'PagingRelease',
        		condition: me.filterCondition ? me.filterCondition + ' AND (' + me.baseCondition + ")": me.baseCondition
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		me.dataCount = res.count;
        		me.getHistoryStore(me.page, me.pageSize);
        	}
        });
	},
	/**
	 * 插入图片组件
	 */
	picture: function(){
		var form = Ext.create('Ext.form.Panel', {
			bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
			items: [{
				xtype: 'filefield',
				name: 'file',
				buttonOnly: true,
		        hideLabel: true,
		        width: 86,
		        height: 17,
				buttonConfig: {
					iconCls: 'x-button-icon-pic',
					text: '插入图片',
					baseCls: 'x-btn',
					cls: 'x-btn-blue'
		        },
		        listeners: {
					change: function(field){
						field.ownerCt.getForm().submit({
		            		url: basePath + 'common/uploadPic.action?em_code=' + em_code+'&caller='+caller,
		            		waitMsg: "正在解析图片信息",
		            		success: function(fp, o){
		            			if(o.result.error){
		            				showError(o.result.error);
		            			} else {
		            				var msg = form.ownerCt.ownerCt.down('htmleditor[name=msg]');
		            		        var element = document.createElement("img");
		            		        element.src = o.result.path;
		            		        element.title = '&img' + o.result.filepath + ";";
		            		        msg.getEl().dom.getElementsByTagName('iframe')[0].contentWindow.document.body.appendChild(element);
		            			}
		            		}
		            	});
					}
				}
			}]
		});
		return form;
	},
	/**
	 * 插入附件组件
	 */
	attach: function(){
		var form = Ext.create('Ext.form.Panel', {
			bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
			items: [{
				xtype: 'filefield',
				name: 'file',
				buttonOnly: true,
		        hideLabel: true,
		        width: 82,
		        height: 17,
				buttonConfig: {
					iconCls: 'x-button-icon-up',
					text: '发送附件',
					cls: 'x-btn-blue'
		        },
		        listeners: {
					change: function(field){
						field.ownerCt.getForm().submit({
		            		url: basePath + 'common/upload.action?em_code=' + em_code,
		            		waitMsg: "正在解析附件信息",
		            		success: function(fp, o){
		            			if(o.result.error){
		            				showError(o.result.error);
		            			} else {
		            				var msg = form.ownerCt.ownerCt.down('htmleditor[name=msg]');
		            		        var element = document.createElement("img");
		            		        element.src = o.result.path;
		            		        element.title = '&img' + o.result.filepath + ";";
		            		        msg.getEl().dom.getElementsByTagName('iframe')[0].contentWindow.document.body.appendChild(element);
		            			}
		            		}
		            	});
					}
				}
			}]
		});
		return form;
	},
	download: function(id, msg){
		Ext.Ajax.request({
        	url : basePath + 'common/getFilePaths.action',
        	async: false,
        	params: {
        		id:  id
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		if(res.files.length > 0){
        			msg = msg.replace("&img" + id + ";", '<img src="' + res.files[0].fp_path + '">');
        		}
        	}
        });
	}
});