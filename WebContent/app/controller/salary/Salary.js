Ext.QuickTips.init();
Ext.define('erp.controller.salary.Salary', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil','erp.util.GridUtil',"erp.view.core.form.MonthDateField"],
    views: ['common.init.Template', 'core.button.UpExcel', 'core.trigger.DbfindTrigger', 'core.trigger.MultiDbfindTrigger',
            'core.toolbar.Toolbar'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.FormUtil=Ext.create('erp.util.FormUtil');
    	this.control({ 
    		'button[id=btn-login]':{
    			afterrender:function(btn){
    				var grid=Ext.getCmp('template');
					Ext.defer(function(){
						me.onLoginClick(grid);
					}, 500);
				}
    		},
    		'button[id=export]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt.down('gridpanel');
    				if(grid){
    					window.location = basePath + 'system/initTemplate.xls?caller=' + grid.caller + 
    						'&title=' + (encodeURI(encodeURI('工资条数据模板')));
    				}
    			}
    		},
    		'button[id=rule]': {
    			click: function(btn){
    				me.showRule(btn.ownerCt.ownerCt.down('gridpanel'));
    			}
    		},
    		'button[id=check]':{
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt.down('gridpanel');
    				if(grid && grid.down('pagingtoolbar').dataCount<=0) showMessage('提示', '无数据');
    				else if(grid) me.checkdata(grid);    				
    			}
    		},
    		'button[id=toformal]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt.down('gridpanel');   				
    				if(grid && grid.ilid > 0) {
    					if(grid.down('pagingtoolbar').dataCount<=0) showMessage('提示', '无数据');{
    						me.toformal(grid);
    						btn.hide();
    						Ext.getCmp("errdelete").hide();
    					}
    						
    				}
    			}
    		},
    		'button[id=todemo]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt.down('gridpanel');   				
    				if(grid && grid.ilid > 0) {
    					if(grid.down('pagingtoolbar').dataCount<=0) showMessage('提示', '无数据');
    					else if(grid.errorNodes && grid.errorNodes.length > 0) {
    						showMessage('提示', '测试未通过,无法转入示例数据');
    					} else {
    						me.todemo(grid);
    					}
    				}
    			}
    		},
    		'button[id=errdelete]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt.down('gridpanel');
    				if(grid && grid.ilid > 0) {
    					me.deleteErrors(grid,btn);
    				}
    			}
    		},
    		'button[id=saveupdates]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt.down('gridpanel');
    				if(grid && grid.ilid > 0) {
    					me.saveUpdates(grid);
    				}
    			}
    		},
    		'button[id=alldownload]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt.down('gridpanel');
    				if(grid && grid.ilid > 0) {
    					window.location = basePath + 'system/initAll.xls?caller=' + grid.caller + 
						'&title=INIT_' + (encodeURI(encodeURI('导出数据'))) + '&id=' + grid.ilid;
    				}
    			}
    		},
    		'button[id=btn-lead]':{
    			click:function(btn){
    				var f=Ext.getCmp('upload');
    				var grid=Ext.getCmp('template-data');
    				grid.store.loadData([]);
    				grid.down('pagingtoolbar').dataCount=0;
        			grid.down('pagingtoolbar').onLoad();
    				if(f){
    					f.show();
    				}else{
    					me.createUploadForm();
    				}
    				Ext.getCmp("la-upload").addCls("la-active");
    				Ext.getCmp("la-check").removeCls("la-active");
    				Ext.getCmp("la-send").removeCls("la-active");		
    				Ext.getCmp("toformal").hide();
    				Ext.getCmp("errdelete").hide();
    				Ext.getCmp("alldownload").hide();
    				Ext.getCmp("rule").show();
					Ext.getCmp("export").show();
					btn.hide();
    			}
    		},
    		'#template': {
    			beforerender:function(panel){
    				Ext.getCmp("la-upload").addCls("la-unactive");
    				Ext.getCmp("la-check").addCls("la-unactive");
    				Ext.getCmp("la-send").addCls("la-unactive");
    			},
    			afterrender: function(p) {
    				this.getDetails(p, caller, false);
    			}
    		},
    	});
    },
    onLoginClick : function(grid) {
		var me = this, win = me.querywin;
		if(login=='true'||login==true){
			Ext.getCmp("la-upload").addCls("la-active");
			Ext.getCmp("rule").show();
			Ext.getCmp("export").show();
			me.createUploadForm().show();
		}else{
			if (!win) {
				var form  = me.createForm(grid);
				var rule=Ext.getCmp('rule');
				rule.disable(true);
				win = me.querywin = Ext.create('Ext.window.Window', {
					closeAction : 'destroy',
					title : '权限验证',
					height: 250,
	        		width: 350,
	        		layout: 'border',
					items : [form],
					buttonAlign : 'center',
					buttons : [{
						text : '确认',
						height : 26,
						iconCls: 'x-button-icon-check',
						handler : function(btn) {
							var time=grid.vetime,i=Ext.getCmp('phonecode').value,pwd=Ext.getCmp('password').value;
							if(time&&i){
									if((new Date().getTime()-time.getTime())<=61*1000){
										Ext.Ajax.request({
											url:basePath+"salary/login.action",
											method:"post",
											params:{
												emcode:em_code,
												password:pwd?pwd:'',	
												phonecode:i,
											},
											callback:function(opts,suc,res){
												var r=Ext.decode(res.responseText);
												if(r.exceptionInfo) {
													showError(r.exceptionInfo);
													return;
												}
												if(r.success){
													grid.vetime=null;
													rule.enable(true);
													Ext.getCmp('password').reset();
													Ext.getCmp("la-upload").addCls("la-active");
													Ext.getCmp("rule").show();
													Ext.getCmp("export").show();
													btn.ownerCt.ownerCt.hide();
													me.createUploadForm().show();
												}else{
													if(r.reason)
													alert(r.reason);
													return;
												}
											}						
										});	
									}else{
										showError('时间超时,请重新获取验证码');
										return;
									}
							}							
						}
					},{
						text : '取消',
						iconCls: 'delete',
						height : 26,
						handler : function(b) {
							b.ownerCt.ownerCt.destroy();
						}
					}],
					listeners:{
						destroy:function(win){
							parent.Ext.getCmp('content-panel').activeTab.close();
						}
					}
				});
			}
			win.show();
		}
	},
	createForm : function(grid) {
	    	var me = this,str;
	    	str=mobile=='null'?'000xxxx0000':mobile.substring(0,3)+'xxxx'+mobile.substring(mobile.length-4);	    		
	    	var form = Ext.create('Ext.form.Panel', {
	    		region: 'center',
	    		anchor: '100% 100%',
	    		layout: 'column',
	    		autoScroll: true,
	    		items:[{
	    			columnWidth: 0.8,
	    	    	xtype: 'textfield',
	    	    	labelWidth: 100,
	    	    	name: 'username',
	    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    	    	fieldLabel: '账&nbsp;&nbsp;号',
	    			fieldCls: 'x-form-field-cir',
	    			value:em_code,
	    			labelAlign : "right",
	    		},{
	    			columnWidth: 0.8,
	    	    	xtype: 'textfield',
	    	    	id: 'password',
	    	    	fieldCls: 'x-form-field-cir',
	    	    	labelWidth: 100,
	    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    	    	fieldLabel: '密&nbsp;&nbsp;码',
	    	    	inputType: 'password',
	    	    	labelAlign : "right",
	    		},{
	    			xtype:'label',
	    			id:'label',
	    			html:'<font id="changeTime" style="color:gray;font:5px Arial;margin:30px 0 10px 70px;">(验证码将发送至'+str+',60秒之内有效!)</font>',
	    		},{
	    			columnWidth: 0.6,
	    	    	xtype: 'textfield',
	    	    	labelWidth: 110,
	    	    	id: 'phonecode',
	    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    	    	fieldLabel: '手机验证码',
	    			fieldCls: 'x-form-field-cir',
	    			labelAlign : "right",
	    		},{
	    			columnWidth: 0.26,
	    	    	xtype: 'button',
	    	    	text:'获取验证码',
	    	    	name: 'phonecode',
	    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
	    			labelAlign : "right",
	    			handler:function(btn){
	    				btn.setDisabled(true);
	    				if(mobile=='null'){
	    					showError("手机号不存在!");
	    					btn.enable(true);
	    					return;
	    				}else{
	    					Ext.Ajax.request({
	    						url:basePath+"salary/verificationCode.action",
	    						method:'post',
	    						params:{
	    						   phone:mobile,
	    						   type:"login"
	    						},
	    						callback:function(opts,suc,res){
	    							var r=Ext.decode(res.responseText);
	    							if(r.success){
	    								grid.vetime=new Date();
	    							//	btn.setDisabled(true);
	    		    					var i=59;
    		    						var id=setInterval(function(){
    		    							btn.setText('( '+i+' )');
    		    							i=i-1;
    		    							if(i<0){
    		    								clearInterval(id);
    		    								btn.setText('获取验证码');
    		    		    					btn.enable(true);
    		    							}
    		    						}, 1000);	
	    							}else{
	    								btn.enable(true);
	    							}
	    						}	    						 
	    					});	    					
	    				}
	    			}
	    		}],
	    		defaults: {
	    			columnWidth: 1,
	    			margin: '4 8 4 8'
	    		},
	    		bodyStyle: 'background:#f1f2f5;',
	    	});
	    	return form;
	    },
	    createUploadForm:function(){
	    	var me=this,date=new Date(),s;
	    	date=date.setMonth(date.getMonth()-1);
	    	var m = new Date(date).getMonth()+1;
	    	if( m < 10) {
	    		m= '0'+m;
	    	}
			s = Number(new Date(date).getFullYear()+''+m);
	    	var form=Ext.create('Ext.window.Window',{
	    		height: 250,
        		width: 350,
        		title:'导入时间',
	    		layout:"fit",
	    		region:'north',
	    		closeAction:"hide",
	    		id:'upload',
	    		items:[{
	    			xtype:'form',
		    		anchor: '100% 100%',
		    		layout: 'column',
		    		bodyStyle: 'background:#f1f2f5;',
		    		autoScroll: true,
		    		defaults: {
		    			margin: '30 20 10 30'
		    		},
		    		buttonAlign : 'center',
		    		items:[{
		    			columnWidth: 0.8,
		    	    	xtype: 'combo',
		    	    	labelWidth: 90,
		    	    	id: 'typefield',
		    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
		    	    	fieldLabel: '发薪类型',
		    			labelAlign : "right",
		    			store:Ext.create('Ext.data.Store', {
		    			    fields: ['abbr', 'name'],
		    			    data : [
		    			        {"abbr":"薪酬", "name":"薪酬"},
		    			        {"abbr":"福利", "name":"福利"},
		    			        {"abbr":"奖金", "name":"奖金"},
		    			        {"abbr":"补贴", "name":"补贴"},
		    			    ]
		    			}),
		    			    queryMode: 'local',
		    			    displayField: 'name',
		    			    valueField: 'abbr',
		    			    value:'薪酬',
		    		},{
		    			columnWidth: 0.8,
		    	    	xtype: 'monthdatefield',
		    	    	labelWidth: 90,
		    	    	id:"datefield",
		    	    	value:s,
		    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
		    	    	fieldLabel: '薪资月份',
		    	    	labelAlign : "right",
		    	    	allowBlank:false,
		    		},
		    	],
		    	buttons:[{
	    			xtype:"upexcel",
	    			width:150,
	    			text:"导入工资条Excel",
	    			iconCls: 'x-data-import',
	    			iconAlign:'center',
	    			listeners:{
	    				afterrender:function(btn){
	    					var grid=Ext.getCmp("template-data");
	    					btn.upexcel = function(field){
	        					if(grid){
	        						btn.getForm().submit({
	            		        		url: basePath + 'system/initImport.action?caller=' +grid.caller,
	            		        		waitMsg: "正在解析Excel",
	            		        		success: function(fp, o){
	            		        			Ext.getCmp('la-check').addCls("la-active");
	            		        			field.reset();
	            		        			grid.ilid = o.result.ilid;
	            		        			grid.down('pagingtoolbar').dataCount =o.result.count;
	            		        			grid.down('pagingtoolbar').onLoad();
	            		        			grid.getGridData(1);
	            		        			btn.ownerCt.ownerCt.ownerCt.close();
	            		        			//Ext.getCmp('alldownload').show();
	            		        			//Ext.getCmp('check').show();
	            		        			Ext.getCmp('toformal').hide();
	            		        			Ext.getCmp('saveupdates').hide();
	            		        			Ext.getCmp("rule").hide();
	            							Ext.getCmp("export").hide();
	            							Ext.getCmp("btn-lead").show();
	            							if(o.result.count>0)me.checkdata(grid);	            							
	            		        		},
	            		        		failure: function(fp, o){
	            		        			if(o.result.size){
	            		        				showError(o.result.error + "&nbsp;" + Ext.util.Format.fileSize(o.result.size));
	            		        				field.reset();
	            		        			} else {
	            		        				showError(o.result.error);
	            		        				field.reset();
	            		        			}
	            		        		}
	            		        	});	        						
	        					}
	        				};
	    				}
	    			}
	    		}]
	    		}],
	    		listeners:{
	    			hide:function(win){
	    				Ext.getCmp("btn-lead").show();
	    			}
	    		}
	    	});
	    	return form;
	    },
    getDetails: function(p, caller, isReload){
    	if(caller != p.caller || isReload){
    		p.caller = caller;
    		var me = this;
        	Ext.Ajax.request({
        		url: basePath + 'system/initDetails.action',
        		params: {
        			caller: caller
        		},
        		method: 'post',
        		callback: function(options, success, response){
        			var res = new Ext.decode(response.responseText);
            		if(res.exceptionInfo != null){
            			Ext.getCmp('upexcel').hide();
            			showError(res.exceptionInfo);return;
            		} else {
            			p.removeAll();
            			var store = me.emptyData(res.data);
            			p.add({
            				xtype: 'grid',
            				id: 'template-data',
            				anchor: "100% 100%",
            				cls: 'custom',
            				caller: caller,
            				ilid: -1,
            				cfg: res.data,//当前导入项的配置信息
            				plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
            			    selModel: Ext.create('Ext.selection.CheckboxModel',{
            			    	headerWidth: 0
            				}),
            				columnLines: true,
            				viewConfig: {
                				style: { overflow: 'hidden', overflowX: 'hidden' }
                				},
            				columns: {},
            				store: store,
            				dockedItems: [me.getDockedItems(store)],
            				getGridData: function(page){
                     			 me.loadInitData(this, page);
             			    },
             			    loadData: function(ilid, count, page){
             			    	this.ilid = ilid;
             			    	this.down('pagingtoolbar').dataCount = count;
    		        			this.down('pagingtoolbar').onLoad();
             			    	this.getGridData(page);
             			    	Ext.getCmp('alldownload').show();
             			    	Ext.getCmp('errdownload').show();
             	    	    	Ext.getCmp('onlyerror').show();
             	    	    	Ext.getCmp('toformal').show();
             	    	    	Ext.getCmp('todemo').show();
             	    	    	Ext.getCmp('errdelete').show();
             	    	    	//Ext.getCmp('saveupdates').show();
             	    	    	//Ext.getCmp('check').show();
             			    },
             			    deleteData: function(ilid){
             			    	me.ondelete(ilid);
             			    },
             			    reset: function(){
             			    	this.ilid = -1;
             			    	this.down('pagingtoolbar').dataCount = 25;
    		        			this.down('pagingtoolbar').onLoad();
    		        			this.store.removeAll(true);
    		        			this.store.loadData([{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}]);
             			    },
             			    showErrors: function(){
             			    	var grid = this,items = grid.store.data.items,
             			    		columns = grid.columns,d,nodes = grid.errorNodes,nodesStr = grid.errorNodesStr,log = '';
             			    	if(nodes && nodes.length > 0) {
             	   	    			Ext.each(items, function(item, index){
                 	    				d = item.data;
                 	    				if(nodesStr.indexOf(d.id_id) > 0) {
                 	    				log = '';
                 	    				Ext.each(columns, function(c, idx){
                 	        	    		if(idx > 0 ){//&& c.logic && c.renderer
                 	        	    			if(Ext.Array.contains(nodes, d.id_id + ':' + c.dataIndex)) {
                 	        	    				if(log == '') {
                 	        	    					log = '<table class="custom-log">';
                 	        	    					log += '<tr class="custom-tr"><td>字段</td><td>值</td>' + 
                 	        	    						'<td>规则</td><td>数据类型</td>' +
                 	        	    						'<td>是否必填</td>' +
                 	        	    						'<td>逻辑</td>' +
                 	        	    						'</tr>';
                 	        	    				}
                 	        	    				log += '<tr><td>' + c.text + '</td><td><font color=red>'
                 	        	    					+ d[c.dataIndex] + '</td><td><font color=blue>'
                 	        	    					+ (c.rule || '') + '</font></td><td><font color=blue>' 
                 	        	    					+ c.dataType + '</font></td><td><font color=blue>'
                 	        	    					+ c.isNeed + '</font></td><td><font color=red>' 
                 	        	    					+ c.logicdesc +'</font></td></tr>';
                 	        	    			}
                 	        	    		} 
                 	        	    	});
                 	    				if(log != '') {
                 	    					log += '</table>';
                 	    				}
                 	    				item.set('log', log);
                 	    				}	
                 	    	    	});
             			    	}
             			    },
            			});
            			var grid = Ext.getCmp('template-data');
            			grid.reconfigure(store, me.parseInitDetails(res.data));
            			var btn = Ext.getCmp('upexcel');
            			if(btn) {
            				btn.show();
                			btn.grid = p.down('gridpanel');
            			}
            		}
        		}
        	});
    	}
    },
    parseInitDetails: function(arr){
    	var me = this, d = new Array(),o,flag;
    //d.push({xtype: 'rownumberer', width: 35});
    	d.push({
    		text: 'ID',
    		dataIndex: 'id_id',
    		hidden: true
    	});
    	d.push({
    		text: '工资月份',
    		dataIndex: 'sl_date',
    		width:90,
    		renderer:function(val){
    			if(val){
    				var date=Ext.Date.parse(val,'Ym');
    				return Ext.Date.format(date,'Y年m月');			
    			}
    		},
    	});
    	d.push({
    		text: '工资类型',
    		dataIndex: 'sl_type',
    		width:70,
    	});
    	Ext.each(arr, function(a){
    		o = new Object();
    		o.text = a.id_caption;
    		o.dataIndex = a.id_field;
    		o.hidden = a.id_visible == 0;
    		o.width = a.id_width;
    		o.rule = a.id_rule;
    		o.dataType = a.id_type;
    		o.isNeed = a.id_need == 1 ? '是' : '否';
    		o.logic = a.id_logic;
    		o.logicdesc = me.parseLogic(o.logic);
    		o.editor = {
    				xtype: 'textfield'
    		};
    		if(o.dataIndex=='sl_date')
    			flag=true;
			if(/combo(.)/.test(o.logic)){
				var s = o.logic.substring(6, o.logic.lastIndexOf(')')).split(','),
					da = new Array();
				Ext.each(s, function(t){
					da.push({
						display: t,
						value: t
					});
				});
				o.editor = {
	    				xtype: 'combobox',
	    				displayField: 'display',
	    				valueField: 'value',
	    				queryMode: 'local',
	    				editable: false,
	    				store:  Ext.create('Ext.data.Store', {
	    		            fields: ['display', 'value'],
	    		            data : da
	    		        })
	    		};
			}
			o.renderer = function(val, meta, record, x, y, store, view){
	    		if(view) {
    				var grid = view.ownerCt,errNodes = grid.errorNodes,cm = grid.columns[y];
        			if(errNodes && y > 0 && !!record.get('log') && Ext.Array.contains(errNodes, record.data['id_id'] + ':' + cm.dataIndex)){
            			meta.tdCls = 'x-td-warn';
            			meta.tdAttr = 'data-qtip="' + (cm.rule || cm.dataType) + '"';
        			}else
        				meta.tdCls = '';
    			}
    			return val;
	    		};	
    		d.push(o);
    	});
    	Ext.each(d,function(dd){
			    dd.filter= {
			         dataIndex: dd.dataIndex,
			         xtype: "textfield",
			      };
				dd.filterJson_={};
		  }) ;
    	return d;
    },
    emptyData: function(arr){
    	var fields = new Array();
    	Ext.each(arr, function(a){
    		fields.push(a.id_field);
    	});
    	return new Ext.data.Store({
    		fields: fields,
    		data: []
    	});
    },
    getDockedItems: function(store){
    	return {
    		xtype: 'pagingtoolbar',
    		store: store,
    		pageSize: 100,
    		dataCount: store.data.items.length,
    		page: 1,
    		dock: 'bottom',
    		displayInfo: true,
    		updateInfo : function(){
    			var page = this.child('#inputItem').getValue() || 1;
    			var me = this,
    				pageSize = me.pageSize || 100,
    				dataCount = me.dataCount || 20;
	 	    	var displayItem = me.child('#displayItem'),
	 	    	 	pageData = me.getPageData();
                pageData.fromRecord = (page-1)*pageSize+1;
                pageData.toRecord = page*pageSize > dataCount ? dataCount : page*pageSize;
	    		pageData.total = dataCount;
	    		var msg;
	                if (displayItem) {
	                    if (me.dataCount === 0) {
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
	            	var me = this,
	            		totalCount = me.dataCount;
		        	return {
		        		total : totalCount,
		        		currentPage : me.page,
		        		pageCount: Math.ceil(me.dataCount / me.pageSize),
		        		fromRecord: ((me.page - 1) * me.pageSize) + 1,
		        		toRecord: Math.min(me.page * me.pageSize, totalCount)
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
		                    	me.page = pageNum;
		                    	me.ownerCt.getGridData(me.page);
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
	            	me.page = value;
	            	me.ownerCt.getGridData(value);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            movePrevious : function(){
	                var me = this;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue() - 1);
	                value = me.child('#inputItem').getValue();
	                me.page = value;
	            	me.ownerCt.getGridData(value);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            moveNext : function(){
	                var me = this,
	                last = me.getPageData().pageCount;
	                total = last;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue() + 1);
	                value = me.child('#inputItem').getValue();
	                me.page = value;
	            	me.ownerCt.getGridData(value);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            moveLast : function(){
	                var me = this,
	                last = me.getPageData().pageCount;
	                total = last;
	                me.child('#inputItem').setValue(last);
	                value = me.child('#inputItem').getValue();
	            	me.page = value;
	            	me.ownerCt.getGridData(value);
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
					c = Math.ceil(e.dataCount / e.pageSize);
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
					var pageCount = this.getPageData().pageCount;
					this.child('#last').setDisabled(value == pageCount || pageCount == 1);
				    this.child('#next').setDisabled(value == pageCount || pageCount == 1);
				    this.child('#first').setDisabled(value <= 1);
				    this.child('#prev').setDisabled(value <= 1);
				}
    	};
    },
    getPathString: function(tree, record){//path:/root/5/12
    	var str = tree.ownerCt.title;
    	return record.getPath('text', '&raquo;').replace('root', str);
    },
    showHistory: function(caller){
    	var w = Ext.create('Ext.Window', {
    		width: '60%',
    		height: '80%',
    		id: 'history-win',
    		title: '导入数据历史记录',
    		autoShow: true,
    		layout: 'anchor',
    		items: [{
    			xtype: 'gridpanel',
    			anchor: '100% 100%',
    			columnLines: true,
    			columns: [{dataIndex: 'il_id', hidden: true},{dataIndex: 'il_caller', hidden: true},{dataIndex: 'il_date', text: '日期', flex: 2},
    			          {dataIndex: 'il_sequence', text: '次数', flex: 1},{dataIndex: 'il_count', text: '数据量(条)', flex: 1},
    			          {dataIndex: 'il_checked', text: '是否已校验', flex: 1, renderer: function(val, m){
    			        	  if(val == '否') {
    			        		  m.style = 'float:right';
    			        	  }
    			        	  return val;
    			          }},{dataIndex: 'il_success',text:'是否校验通过', flex: 1, renderer: function(val, m){
    			        	  if(val == '否') {
    			        		  m.style = 'float:right';
    			        	  }
    			        	  return val;
    			          }},
    			          {dataIndex: 'il_toformal', text: '是否转正式', flex: 1, renderer: function(val, m){
    			        	  if(val == '否') {
    			        		  m.style = 'float:right';
    			        	  }
    			        	  return val;
    			          }}, {text: '', flex: 1, renderer: function(val, m, r){
    			        	  return '<a href="javascript:Ext.getCmp(\'template-data\').loadData(' + r.get('il_id') + ',' 
    			        	  		+ r.get('il_count') + ',1);Ext.getCmp(\'history-win\').close();Ext.getDom(\'check\').click();">载入</a>&nbsp;&nbsp;<a href="javascript:Ext.getCmp(\'template-data\').deleteData(' + 
    			        	  		r.get('il_id') + ');Ext.getCmp(\'history-win\').close();">删除</a>';
    			          }}],
    			store: Ext.create('Ext.data.Store', {
    				fields: ['il_id', 'il_caller', 'il_date', 'il_sequence', 'il_count', 'il_checked', 'il_success', 'il_toformal'],
    				data: [{},{},{},{},{},{},{},{},{},{}]
    			})
    		}]
    	});
    	this.getInitLog(w.down('gridpanel'), caller);
    },
    getInitLog: function(g, c){
    	Ext.Ajax.request({
    		url: basePath + 'system/initHistory.action',
    		params: {
    			caller: c
    		},
    		method: 'post',
    		callback: function(opt, s, r){
    			var res = new Ext.decode(r.responseText);
    			var dd = res.data;
    			Ext.each(dd, function(d){
    				d.il_date = Ext.Date.format(new Date(d.il_date), 'Y-m-d H:i:s');
    				d.il_checked = d.il_checked == 1 ? '是' : '否';
    				d.il_success = d.il_success == 1 ? '是' : '否';
    				d.il_toformal = d.il_toformal == 1 ? '是' : '否';
    			});
    			g.store.loadData(dd);
    		}
    	});
    },
    loadInitData: function(grid, page){
    	var date=Ext.getCmp("datefield").value;
    	var type=Ext.getCmp("typefield").value;
    	grid.date=date;
    	grid.type=type;
    	var f = (page-1) * 100 + 1,
    		t = page*100;
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'system/getInitData.action',
    		params: {
    			condition: "id_ilid=" + grid.ilid + 
    				" AND id_detno between " + f + " AND " + t
    		},
    		method: 'post',
    		callback: function(options, success, response){
    			var res = new Ext.decode(response.responseText);
    			grid.setLoading(false);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		} else {
        			var datas = new Array(), o;
        			Ext.each(res.data, function(d){
        				o = Ext.decode(d.id_data);
        				if(date)
        				o.sl_date=date;
        				o.id_id = d.id_id;
        				o.sl_type=type;
        				datas.push(o);
        			});
        			
        			grid.store.loadData(datas);
        			grid.showErrors();
        		}
    		}
    	});
    },
    loadErrData: function(grid, page){
    	var f = (page-1) * 100 + 1,
    		t = page*100;
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'system/getErrData.action',
    		timeout: 10000,
    		params: {
    			id:grid.ilid,
    			condition:" RN between " + f + " AND " + t
    		},
    		method: 'post',
    		callback: function(options, success, response){
    			grid.setLoading(false);
    			if(!success) {
    				alert('系统繁忙，请稍后再试!');
    				return;
    			}
    			var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		} else {
        			var datas = new Array(), o;
        			Ext.each(res.data, function(d){
        				o = Ext.decode(d.id_data);
        				o.id_id = d.id_id;
        				datas.push(o);
        			});
        			grid.store.loadData(datas);
        			grid.showErrors();
        		}
    		}
    	});
    },
    ondelete: function(id){
    	var grid = Ext.getCmp('template-data');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'system/deleteInitData.action',
    		params: {
    			id: id
    		},
    		method: 'post',
    		callback: function(options, success, response){
    			var res = new Ext.decode(response.responseText);
    			grid.setLoading(false);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		} else {
        			if(id == grid.ilid) {//如果删除的是当前显示的数据，要清除当前grid的数据
        				grid.reset();
        			}
        		}
    		}
    	});
    },
    showRule: function(grid){
    	if(grid){
    		var me = this,data = me.getRules(grid);
    		Ext.create('Ext.window.Window', {
    			width: '100%',
    			height: '100%',
    			autoShow: 'true',
    			closeAction: 'destroy',
    			title: '导入配置',
    			layout: 'anchor',
    			tbar: ['->',{
    				text: '保存',
    				width: 80,
    				cls: 'x-btn-bar-s',
    				handler: function(b){
    					me.saveInitDetails(b.ownerCt.ownerCt.down('gridpanel'), grid.caller);
    				}
    			},{
					xtype: 'button',
					width: 100,
					cls: 'x-btn-bar-s',
					text: '导出方案',
					handler: function() {
						window.open(basePath + 'common/dump/exp.action?type=Import&identity=' + grid.caller);
					}
				},{
    				text: '取消',
    				width: 80,
    				cls: 'x-btn-bar-s',
    				handler: function(b) {
    					b.ownerCt.ownerCt.close();
    				}
    			},'->'],
    			items: [{
    				xtype: 'gridpanel',
    				id: 'initdetails',
    				anchor: '100% 100%',
    				plugins: Ext.create('Ext.grid.plugin.CellEditing', {
    			        clicksToEdit: 1
    			    }),
    				columnLines: true,
    				keyField: 'id_id',
    				detno: 'id_detno',
    				bbar: {xtype: 'erpToolbar'},
    				dbfinds: [{
    					field: 'id_table',
    					dbGridField: 'dd_tablename;ddd_tablename'
    				},{
    					field: 'id_field',
    					dbGridField: 'ddd_fieldname'
    				},{
    					field: 'id_caption',
    					dbGridField: 'ddd_description'
    				},{
    					field: 'id_type',
    					dbGridField: 'ddd_fieldtype'
    				}],
    				columns: [{
    					dataIndex: 'id_id',
    					hidden: true
    				},{
    					dataIndex: 'id_caller',
    					hidden: true,
    					renderer: function(v, m, r, x, y, s, w){
    						var grid = w.ownerCt;
    						if(Ext.isEmpty(v) && !Ext.isEmpty(grid.caller)) {
    							v = grid.caller;
    							r.set('id_caller', v);
    						}
    						return v;
    					}
    				},{
    					dataIndex: 'id_detno',
    					text: '序号',
    					width: 40,
    					renderer: function(val, meta) {
    				        meta.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
    				        return val;
    				    }
    				},{
    					text: '导入表',
    					dataIndex: 'id_table',
    					width: 90,
    					editor: {
    						xtype: 'dbfindtrigger'
    					},
    					dbfind: 'DataDictionary|dd_tablename'
    				},{
    					text: '导入项',
    					dataIndex: 'id_field',
    					width: 100,
    					editor: {
    						xtype: 'multidbfindtrigger'
    					},
    					dbfind: 'DataDictionaryDetail|ddd_fieldname'
    				},{
    					text: '描述',
    					dataIndex: 'id_caption',
    					width: 105,
    					renderer: function(v){
    						if(Ext.isEmpty(v)) {
    							return '<font color=gray>无</font>';
    						}
    						return v;
    					},
    					editor: {
    						xtype: 'textfield'
    					}
    				},{
    					text: '类型',
    					dataIndex: 'id_type',
    					width: 105,
    					renderer: function(v, meta, record){
    						if(Ext.isEmpty(v)) {
    							return '<font color=gray>无</font>';
    						} else {
    							v = v.toLowerCase();
    							if(contains(v, 'varchar2')) {
    								v = v.replace(/byte/g, '').trim();
    							}
    							if(record.data['id_type'] != v) {
									record.set('id_type', v);
								}
    						}
    						return v;
    					},
    					editor: {
    						xtype: 'trigger',
    						editable: false,
    						triggerCls: 'x-form-search-trigger',
    						onTriggerClick: function(){
    							me.dataTypes(this, Ext.getCmp('initdetails').selModel.lastSelected);
    						}
    					}
    				},{
    					text: '默认值',
    					dataIndex: 'id_default',
    					wdith: 120,
    					renderer: function(v){
    						if(Ext.isEmpty(v)) {
    							return '<font color=gray>无</font>';
    						}
    						return v;
    					},
    					editor: {
    						xtype: 'trigger',
    						editable: false,
    						triggerCls: 'x-form-search-trigger',
    						onTriggerClick: function(){
    							me.fieldTypes(this, Ext.getCmp('initdetails').selModel.lastSelected);
    						}
    					}
    				},{
    					text: '字段类型',
    					dataIndex: 'id_fieldtype',
    					wdith: 50,
    					xtype:'combocolumn',
    					editor: {
    						xtype: 'combo',
    						wdith: 50,
    						listWidth:50,
    						displayField: 'display',
    						valueField: 'value',
    						queryMode: 'local',
    						editable: false,
    						value: 0,
    						store:{
    							fields: ['display','value'],
    							data:[{display: '主表', value:0},
    							      {display: '从表', value:1},
    							      {display: '其它', value:2}]	
    						}
    					},
    					renderer: function(val, meta, record, x, y, store, view) {
    						var grid = view.ownerCt, cn = grid.columns[y], r = val;
    						if(cn.editor) {
    							Ext.Array.each(cn.editor.store.data, function(){
    								if(val == this.value)
    									r = this.display;
    							});
    						}
    						return r;
    					}
    				},{
    					text: '是否显示',
    					dataIndex: 'id_visible',
    					xtype: 'checkcolumn',
    					width: 75,
    					editor: {
    						xtype: 'checkbox'
    					}
    				},{
    					text: '是否必填',
    					dataIndex: 'id_need',
    					xtype: 'checkcolumn',
    					width: 75,
    					editor: {
    						xtype: 'checkbox'
    					}
    				},{
    					text: '列宽度',
    					dataIndex: 'id_width',
    					xtype: 'numbercolumn',
    					format: '0',
    					width: 75,
    					editor: {
    						xtype: 'numberfield',
    						hideTrigger: true,
    						format: '0'
    					}
    				},{
    					text: '逻辑表达式',
    					dataIndex: 'id_logic',
    					width: 0,
    					renderer: function(v){
    						if(Ext.isEmpty(v)) {
    							return '<font color=gray>无</font>';
    						}
    						return v;
    					},
    					editor: {
    						xtype: 'trigger',
    						editable: false,
    						triggerCls: 'x-form-search-trigger',
    						onTriggerClick: function(){
    							me.logicTypes(this, Ext.getCmp('initdetails').selModel.lastSelected);
    						}
    					}
    				},{
    					text: '逻辑描述',
    					dataIndex: 'id_rule',
    					width: 0,
    					renderer: function(v){
    						if(Ext.isEmpty(v)) {
    							return '<font color=gray>无</font>';
    						}
    						return v;
    					},
    					editor: {
    						xtype: 'textfield'
    					}
    				}],
    				store: Ext.create('Ext.data.Store', {
    					fields: ['id_id', 'id_caller', 'id_detno', 'id_table', 'id_field', 'id_caption', 'id_logic', 'id_rule',
    					         'id_type', {name: 'id_visible', type: 'bool'},  {name: 'id_need', type: 'bool'}, 'id_default',
    					         'id_width','id_fieldtype'],
    					data: data
    				}),
    				listeners: {
    					itemclick: function(selModel, record){
    						me.GridUtil.onGridItemClick(selModel, record);
    					}
    				}
    			}]
    		});
    		me.control('field[name=id_field]', {
    			focus: function(t){
					var record = Ext.getCmp('initdetails').selModel.lastSelected;
    				var dd = record.data['id_table'];
    				if(!Ext.isEmpty(dd)) {
    					t.dbBaseCondition = 'ddd_tablename=\'' + dd + '\'';
    				} else {
    					t.dbBaseCondition = '';
    				}
				}
    		});
    	}
    },
    saveInitDetails: function(grid, caller){
    	var me = this,datas = [],items = grid.store.data.items,d;
    	Ext.each(items, function(i){
    		if(i.dirty) {
    			d = i.data;
    			if(!Ext.isEmpty(d.id_field) && !Ext.isEmpty(d.id_table)) {
    				d.id_field=d.id_field.trim();
    				d.id_need = d.id_need ? 1 : 0;
        			d.id_visible = d.id_visible ? 1 : 0;
        			d.id_caller = caller;
        			d.id_fieldtype =Ext.isEmpty(d.id_fieldtype)? 0:d.id_fieldtype;
        			d.id_width = Ext.isEmpty(d.id_width) ? 100 : d.id_width;
        			datas.push(d);
    			}
    		}
    	});
    	Ext.Ajax.request({
    		url: basePath + 'system/saveInitDetail.action',
    		method: 'post',
    		params: {
    			data: unescape(Ext.encode(datas).replace(/\\/g,"%"))
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(!rs || rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    			} else {
    				alert('保存成功!');
    				grid.ownerCt.destroy();
    				me.getDetails(Ext.getCmp('template'), caller, true);
    			}
    		}
    	});
    },
    fieldTypes: function(f, record){
    	var g = Ext.getCmp('initdetails'), dd = g.store.data.items,ff = [];
    	Ext.each(dd, function(i){
    		if(i.data.id_field != record.data.id_field) {
    			ff.push({
        			display: i.data.id_caption,
        			value: i.data.id_field
        		});
    		}
    	});
    	var ww = Ext.create('Ext.Window', {
			autoShow: true,
			modal: true,
			title: '默认值',
			width: '55%',
			height: '50%',
			layout: 'column',
			items: [{
				xtype: 'fieldcontainer',
				name: 'keyField',
				columnWidth: 1,
				items: [{
					xtype: 'radio',
					columnWidth: 1,
					boxLabel: '自动取ID',
					name: 'default'
				}],
				getValue: function(){
					return 'keyField';
				},
				setValue: function(){
					this.down('radio').setValue(true);
				}
			},{
				xtype: 'fieldcontainer',
				name: 'codeField',
				columnWidth: 1,
				items: [{
					xtype: 'radio',
					columnWidth: 1,
					boxLabel: '自动取编号',
					name: 'default'
				}],
				getValue: function(){
					return 'codeField';
				},
				setValue: function(){
					this.down('radio').setValue(true);
				}
			},{
				xtype: 'fieldcontainer',
				name: 'date',
				columnWidth: 1,
				layout: 'hbox',
				items: [{
					xtype: 'radio',
					boxLabel: '当前时间',
					name: 'default'
				},{
					xtype: 'checkbox',
					inputValue: 'yy-MM-dd',
					separator: ' ',
					boxLabel: '年月日'
				},{
					xtype: 'checkbox',
					inputValue: 'HH:mm:ss',
					separator: '',
					boxLabel: '时分秒'
				}],
				getValue: function(){
					var rr = this.query('checkbox[checked=true]'), vv = '', len = rr.length;
					Ext.each(rr, function(r, idx){
						if(r.xtype == 'checkbox' && r.inputValue) {
							vv += r.inputValue;
							if(idx < len - 1) {
								vv += r.separator;
							}
						}
					});
					if(vv == '') {
						return '';
					}
					return 'date(' + vv + ')';
				},
				setValue: function(v){
					this.down('radio').setValue(true);
					v = v.substring(v.indexOf('(') + 1, v.indexOf(')'));
					var y = v.split(' '),arr,me = this;				
					Ext.each(y, function(i){
						arr = me.down('checkbox[inputValue=' + i + ']');
						if(arr)
							arr.setValue(true);
					});
				}
			},{
				xtype: 'fieldcontainer',
				name: 'replace',
				columnWidth: 1,
				layout: 'hbox',
				items: [{
					xtype: 'radio',
					name: 'default',
					boxLabel: '替代值'
				},{
					
					xtype: 'fieldcontainer',
					items: [{
						xtype: 'textfield',
						emptyText: '显示值'
					},{
						xtype: 'textfield',
						emptyText: '实际值'
					}],
					getValue: function(){
						var val = this.items.items[0],dis = this.items.items[1];
						if(!Ext.isEmpty(val.value) && !Ext.isEmpty(dis.value)) {
							return val.value + ':' + dis.value;
						}
						return '';
					},
					setValue: function(v) {
						if(!Ext.isEmpty(v)) {
							this.items.items[0].setValue(v.split(':')[0]);
							this.items.items[1].setValue(v.split(':')[1] || '');
						}
					}
				},{
					xtype: 'button',
					iconCls: 'x-button-icon-add',
					cls: 'x-btn-tb',
					handler: function(b){
						b.ownerCt.insert(b.ownerCt.items.items.length - 1, {
							xtype: 'fieldcontainer',
							items: [{
								xtype: 'textfield',
								emptyText: '显示值'
							},{
								xtype: 'textfield',
								emptyText: '实际值'
							}],
							getValue: function(){
								var val = this.items.items[0],dis = this.items.items[1];
								if(!Ext.isEmpty(val.value) && !Ext.isEmpty(dis.value)) {
									return val.value + ':' + dis.value;
								}
								return '';
							},
							setValue: function(v) {
								if(!Ext.isEmpty(v)) {
									this.items.items[0].setValue(v.split(':')[0]);
									this.items.items[1].setValue(v.split(':')[1] || '');
								}
							}
						});
					}
				}],
				getValue: function(){
					var tx = this.query('fieldcontainer'),val = '';
					Ext.each(tx, function(t){
						if(!Ext.isEmpty(t.getValue())) {
							if(val == '') {
								val = t.getValue();
							} else {
								val += ',' + t.getValue();
							}
						}
					});
					if(val == '') {
						return null;
					}
					return 'replace(' +val + ')';
				},
				setValue: function(v){
					var m = this;
					this.down('radio').setValue(true);
					var t = v.substring(v.indexOf('(') + 1, v.lastIndexOf(')')).split(',');
					Ext.each(t, function(r, i){
						if(i == 0) {
							m.down('fieldcontainer').setValue(r);
						} else {
							var f = Ext.create('Ext.form.FieldContainer', {
								items: [{
									xtype: 'textfield',
									emptyText: '显示值'
								},{
									xtype: 'textfield',
									emptyText: '实际值'
								}],
								getValue: function(){
									var val = this.items.items[0],dis = this.items.items[1];
									if(!Ext.isEmpty(val.value) && !Ext.isEmpty(dis.value)) {
										return val.value + ':' + dis.value;
									}
									return '';
								},
								setValue: function(v) {
									if(!Ext.isEmpty(v)) {
										this.items.items[0].setValue(v.split(':')[0]);
										this.items.items[1].setValue(v.split(':')[1] || '');
									}
								}
							});
							m.insert(m.items.items.length - 1, f);
							f.setValue(r);
						}
					});
				}
			},{
				xtype: 'fieldcontainer',
				name: 'copy',
				columnWidth: 1,
				layout: 'hbox',
				items: [{
					xtype: 'radio',
					boxLabel: '等于字段',
					name: 'default'
				},{
					xtype: 'combo',
					displayField: 'display',
					valueField: 'value',
					queryMode: 'local',
					editable: false,
					store: Ext.create('Ext.data.Store', {
						fields: ['display','value'],
						data: ff
					})
				},{
					xtype: 'radio',
					name: 'condition',
					inputValue: 'if',
					boxLabel: '只在值为空时'
				},{
					xtype: 'radio',
					name: 'condition',
					inputValue: 'of',
					boxLabel: '任何情况下',
					checked: true
				}],
				getValue: function(){
					var rr = this.query('radio[name=condition]'), vv = '';
					Ext.each(rr, function(){
						if(this.checked) {
							vv = this.inputValue;
						}
					});
					return 'copy' + vv + '(' + this.down('combo').value + ')';
				},
				setValue: function(v) {
					this.down('radio').setValue(true);
					this.down('combo').setValue(v.substring(v.indexOf('(') + 1, v.lastIndexOf(')')));
					var ff = v.substring(4, v.indexOf('('));
					this.down('radio[inputValue=' + ff + ']').setValue(true);
				}
			},{
				xtype: 'fieldcontainer',
				name: 'others',
				columnWidth: 1,
				layout: 'hbox',
				items: [{
					xtype: 'radio',
					boxLabel: '其它',
					name: 'default'
				},{
					xtype: 'textfield'
				}],
				getValue: function() {
					return this.down('textfield').value;
				},
				setValue: function(v) {
					this.down('radio').setValue(true);
					this.down('textfield').setValue(v);
				}
			}],
			buttonAlign: 'center',
			buttons: [{
				text: '确定',
				handler: function(b){
					var w = b.ownerCt.ownerCt;
					var val = '',ch = w.query('radio[name="default"]'),cc = null;
					Ext.each(ch, function(){
						if(this.checked) {
							cc = this;
						}
					});
					if(cc) {
						v = cc.ownerCt.getValue();
						if(v != null) {
							val = v;
						}
					}
					f.setValue(val);
					record.set('id_default', val);
					w.close();
				}
			},{
				text: '取消',
				handler: function(b){
					b.ownerCt.ownerCt.close();
				}
			}]
    	});
    	var fc = ww.query('fieldcontainer');
    	if(!Ext.isEmpty(f.value)) {
    		var vv = f.value,bool = false;
    		Ext.each(fc, function(r){
	    		if(contains(vv, r.name, true)) {
	    			bool = true;
	    			r.setValue(vv);
	    		}
	    	});
    		if(!bool) {
    			ww.down('fieldcontainer[name=others]').setValue(vv);
    		}
    	}
    },
    logicTypes: function(f, record){
    	var g = Ext.getCmp('initdetails'), dd = g.store.data.items,ff = [];
    	Ext.each(dd, function(i){
    		if(i.data.id_field != record.data.id_field) {
    			ff.push({
        			display: i.data.id_caption,
        			value: i.data.id_field
        		});
    		}
    	});
    	var ww = Ext.create('Ext.Window', {
			autoShow: true,
			modal: true,
			title: '逻辑表达式',
			width: '80%',
			height: '60%',
			layout: 'column',
			padding: 15,
			items: [{
				xtype: 'fieldcontainer',
				name: 'unique',
				columnWidth: 1,
				items: [{
					xtype: 'checkbox',
					boxLabel: '唯一性'
				}],
				getValue: function(){
					return 'unique(' + record.data.id_table + '|' + record.data.id_field + ')';
				},
				setValue: function(v){
					this.down('checkbox').setValue(true);
				}
			},{
				xtype: 'fieldcontainer',
				name: 'upper',
				columnWidth: 1,
				items: [{
					xtype: 'checkbox',
					boxLabel: '强制转大写'
				}],
				getValue: function(){
					return 'upper(' + record.data.id_field + ')';
				},
				setValue: function(v){
					this.down('checkbox').setValue(true);
				}
			},{
				xtype: 'fieldcontainer',
				name: 'trim',
				columnWidth: 1,
				layout: 'column',
				items: [{
					xtype: 'checkbox',
					columnWidth: 0.1,
					boxLabel: '禁用字符'
				},{
					xtype: 'textfield',
					columnWidth: 0.1
				},{
					xtype: 'button',
					iconCls: 'x-button-icon-add',
					cls: 'x-btn-tb',
					handler: function(b){
						b.ownerCt.insert(b.ownerCt.items.items.length - 1, {
							xtype: 'textfield',
							columnWidth: 0.1
						});
					}
				}],
				getValue: function(){
					var tx = this.query('textfield'),val = '';
					Ext.each(tx, function(t){
						if(!Ext.isEmpty(t.value)) {
							if(val == '') {
								val = t.value;
							} else {
								val += ',' + t.value;
							}
						}
					});
					if(val == '') {
						return null;
					}
					return 'trim(' +val + ')';
				},
				setValue: function(v){
					var m = this;
					m.down('checkbox').setValue(true);
					var t = v.substring(v.indexOf('(') + 1, v.lastIndexOf(')')).split(',');
					Ext.each(t, function(r, i){
						if(i == 0) {
							m.down('textfield').setValue(r);
						} else {
							m.insert(m.items.items.length - 1, {
								xtype: 'textfield',
								columnWidth: 0.1,
								value: r
							});
						}
					});
				}
			},{
				xtype: 'fieldcontainer',
				name: 'combo',
				columnWidth: 1,
				layout: 'column',
				items: [{
					xtype: 'checkbox',
					columnWidth: 0.1,
					boxLabel: '可选范围'
				},{
					xtype: 'textfield',
					columnWidth: 0.1
				},{
					xtype: 'button',
					iconCls: 'x-button-icon-add',
					cls: 'x-btn-tb',
					handler: function(b){
						b.ownerCt.insert(b.ownerCt.items.items.length - 1, {
							xtype: 'textfield',
							columnWidth: 0.1
						});
					}
				}],
				getValue: function(){
					var tx = this.query('textfield'),val = '';
					Ext.each(tx, function(t){
						if(!Ext.isEmpty(t.value)) {
							if(val == '') {
								val = t.value;
							} else {
								val += ',' + t.value;
							}
						}
					});
					if(val == '') {
						return null;
					}
					return 'combo(' +val + ')';
				},
				setValue: function(v){
					var m = this;
					m.down('checkbox').setValue(true);
					var t = v.substring(v.indexOf('(') + 1, v.lastIndexOf(')')).split(',');
					Ext.each(t, function(r, i){
						if(i == 0) {
							m.down('textfield').setValue(r);
						} else {
							m.insert(m.items.items.length - 1, {
								xtype: 'textfield',
								columnWidth: 0.1,
								value: r
							});
						}
					});
				}
			},{
				xtype: 'fieldcontainer',
				name: 'accord',
				columnWidth: 1,
				layout: 'column',
				items: [{
					xtype: 'checkbox',
					columnWidth: 0.1,
					boxLabel: '关联'
				},{
					columnWidth: 0.3,
					emptyText: '表',
					id: 'accord_table',
					name: 'accord_table',
					xtype: 'dbfindtrigger'
				},{
					columnWidth: 0.3,
					emptyText: '字段',
					id: 'accord_field',
					name: 'accord_field',
					dbKey: 'accord_table',
					mappingKey: 'ddd_tablename',
					dbMessage: '请选择表名',
					xtype: 'dbfindtrigger'
				}],
				getValue: function(){
					var at = this.down('dbfindtrigger[name=accord_table]'),
						af = this.down('dbfindtrigger[name=accord_field]');
					if(!Ext.isEmpty(at.value) && !Ext.isEmpty(af.value)) {
						return 'accord(' + at.value + '|' + af.value + ')';
					}
					return null;
				},
				setValue: function(v){
					this.down('checkbox').setValue(true);
					var t = v.substring(v.indexOf('(') + 1, v.lastIndexOf(')')).split('|');
					this.down('dbfindtrigger[name=accord_table]').setValue(t[0]);
					this.down('dbfindtrigger[name=accord_field]').setValue(t[1]);
				}
			},{
				xtype: 'fieldcontainer',
				name: 'diffence',
				columnWidth: 1,
				layout: 'column',
				items: [{
					xtype: 'checkbox',
					columnWidth: 0.1,
					boxLabel: '不同于'
				},{
					columnWidth: 0.3,
					xtype: 'combo',
					displayField: 'display',
					valueField: 'value',
					queryMode: 'local',
					editable: false,
					store: Ext.create('Ext.data.Store', {
						fields: ['display','value'],
						data: ff
					})
				}],
				getValue: function(){
					var c = this.down('combo');
					if(!Ext.isEmpty(c.value)) {
						return 'diffence(' + c.value + ')';
					}
					return null;
				},
				setValue: function(v){
					this.down('checkbox').setValue(true);
					this.down('combo').setValue(v.substring(v.indexOf('(') + 1, v.lastIndexOf(')')));
				}
			},{
				xtype: 'fieldcontainer',
				name: 'combine',
				columnWidth: 1,
				layout: 'column',
				items: [{
					xtype: 'checkbox',
					columnWidth: 0.1,
					boxLabel: '组合校验'
				},{
					columnWidth: 0.3,
					xtype: 'combo',
					displayField: 'display',
					valueField: 'value',
					queryMode: 'local',
					multiSelect: true,
					editable: false,
					store: Ext.create('Ext.data.Store', {
						fields: ['display','value'],
						data: ff
					})
				}],
				getValue: function(){
					var c = this.down('combo');
					if(!Ext.isEmpty(c.value)) {
						return 'combine(' + c.value + ')';
					}
					return null;
				},
				setValue: function(v){
					this.down('checkbox').setValue(true);
					this.down('combo').setValue(v.substring(v.indexOf('(') + 1, v.lastIndexOf(')')));
				}
			},{
				xtype: 'fieldcontainer',
				name: 'minValue',
				columnWidth: 1,
				layout: 'column',
				items: [{
					xtype: 'checkbox',
					columnWidth: 0.1,
					boxLabel: '最小值'
				},{
					columnWidth: 0.3,
					xtype: 'numberfield'
				}],
				getValue: function(){
					var c = this.down('numberfield');
					if(!Ext.isEmpty(c.value)) {
						return 'minValue(' + c.value + ')';
					}
					return null;
				},
				setValue: function(v){
					this.down('checkbox').setValue(true);
					this.down('minValue').setValue(v.substring(v.indexOf('(') + 1, v.lastIndexOf(')')));
				}
			}],
			buttonAlign: 'center',
			buttons: [{
				text: '确定',
				handler: function(b){
					var w = b.ownerCt.ownerCt;
					var val = '',ch = w.query('checkbox[checked=true]'),v;
					Ext.each(ch, function(c){
						v = c.ownerCt.getValue();
						if(v != null) {
							if(val != '') {
								val += ';';
							}
							val += v;
						}
					});
					f.setValue(val);
					record.set('id_logic', val);
					w.close();
				}
			},{
				text: '取消',
				handler: function(b){
					b.ownerCt.ownerCt.close();
				}
			}]
    	});
    	var fc = ww.query('fieldcontainer');
    	if(!Ext.isEmpty(f.value)) {
    		var vv = f.value.split(';');
    		Ext.each(vv, function(s){
    			Ext.each(fc, function(r){
    	    		if(contains(s, r.name, true)) {
    	    			r.setValue(s);
    	    		}
    	    	});
    		});
    	}
    },
    dataTypes: function(f, record){
    	var v = f.value,a = v.substring(v.indexOf('(') + 1, v.indexOf(')')),x = 30,y = 0;
    	if(a) {
    		x = a.split(',')[0];
    		y = a.split(',')[1];
    	}
    	var isDate = contains(f.value, 'date', true), isStr = contains(f.value, 'varchar2', true);
    	Ext.create('Ext.Window', {
			autoShow: true,
			modal: true,
			title: '数据类型',
			width: '40%',
			height: '30%',
			items: [{
				xtype: 'container',
				padding: '10 5 5 10',
				items: [{
					xtype: 'radio',
					boxLabel: '日期',
					name: 'type',
					checked: isDate,
					inputValue: 'date()',
					listeners: {
						change : function(){
							if(this.checked) {
								this.ownerCt.down('numberfield[name=length_y]').hide();
								this.ownerCt.down('numberfield[name=length_x]').hide();
							} else {
								this.ownerCt.down('numberfield[name=length_x]').show();
							}
						}
					}
				},{
					xtype: 'radio',
					boxLabel: '字符串',
					name: 'type',
					checked: isStr,
					inputValue: 'varchar2'
				},{
					xtype: 'radio',
					boxLabel: '数字',
					name: 'type',
					checked: !isDate && !isStr,
					inputValue: 'number',
					listeners: {
						change : function(){
							if(this.checked) {
								this.ownerCt.down('numberfield[name=length_y]').show();
								this.ownerCt.down('numberfield[name=length_x]').setMaxValue(38);
								this.ownerCt.down('numberfield[name=length_x]').validate();
							} else {
								this.ownerCt.down('numberfield[name=length_y]').hide();
								this.ownerCt.down('numberfield[name=length_x]').setMaxValue(2000);
							}
						}
					}
				},{
					xtype: 'numberfield',
					fieldLabel: '长度',
					name: 'length_x',
					format: '0',
					value: x,
					hidden: isDate,
					maxText: '超过最大长度',
					listeners: {
						change: function(f){
							var a = f.ownerCt.down('radio[checked=true]').inputValue;
							if(a == 'number') {
								f.setMaxValue(38);
							} else {
								f.setMaxValue(2000);
							}
						}
					}
				},{
					xtype: 'numberfield',
					fieldLabel: '小数位数',
					name: 'length_y',
					hidden: isDate || isStr,
					format: '0',
					maxValue: 57,
					maxText: '超过最大数57',
					value: y
				}]
			}],
			buttonAlign: 'center',
			buttons: [{
				text: '确定',
				handler: function(b){
					var w = b.ownerCt.ownerCt;
					v = w.down('radio[checked=true]').inputValue;
					x = w.down('numberfield[name=length_x]').value || 0;
					y = w.down('numberfield[name=length_y]').value || 0;
					if(v == 'varchar2') {
						v += '(' + x + ')';
					} else if (v == 'number') {
						v += '(' + x + ',' + y + ')';
					}
					f.setValue(v);
					record.set(f.name, v);
					w.close();
				}
			},{
				text: '取消',
				handler: function(b){
					b.ownerCt.ownerCt.close();
				}
			}]
		});
    },
    getRules: function(grid){
    	var datas = new Array(),str=new Array("sl_date","sl_type","sl_recorder","sl_recordercode","sl_recorddate","sl_remark","sl_status","sl_result","sl_fpid");
    	if(grid.cfg && grid.cfg.length > 0) {
    		var cfgs=grid.cfg;
    		Ext.each(cfgs,function(cfg){
    			if(!Ext.Array.contains(str,cfg.id_field)){
    				datas.push(cfg);
    			}
    		});
    		var keys;
    		Ext.each(datas, function(d){
    			keys = Ext.Object.getKeys(d);
    			Ext.each(keys, function(k){
    				d[k] = d[k] == null ? '' : d[k];
    			});
    			d.id_visible = d.id_visible == 1;
    			d.id_need = d.id_need == 1;
    		});
    	} else {
    		datas = [{id_detno:1,id_width:100},{id_detno:2,id_width:100},{id_detno:3,id_width:100},
    		         {id_detno:4,id_width:100},{id_detno:5,id_width:100},{id_detno:6,id_width:100},
    		         {id_detno:7,id_width:100},{id_detno:8,id_width:100},{id_detno:9,id_width:100},
    		         {id_detno:10,id_width:100}];
    	}
    	return datas;
    },
    /**
     * 数据校验
     */
    checkdata: function(grid){
    	var me = this;
    	var p = Ext.create('Ext.ProgressBar', {
			width: '60%',
			text: '准备校验中...',
			floating: true,
			renderTo: Ext.getBody()
		}).show();
    	grid.setLoading(true);
    	//先删除上次校验记录
    	me.beforeCheck(grid, p, function(){
    		// 准备交易环境
    		me.onCheck(grid, p, function(){
    			// 校验+清除校验环境
    			me.afterCheck(grid, p, function(){
    				//刷新grid.renderer
    				me.getCheckResult(grid, p);
    			});
    		});
    	});
    },
    beforeCheck: function(grid, process, callback) {
    	Ext.Ajax.request({
    		url: basePath + 'system/beforeCheckLog.action',
    		params: {
    			id: grid.ilid
    		},
    		method: 'post',
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText);
    			if(res.success) {
    				callback.call();
    			} else {
    				alert('系统错误，准备校验失败!');
    				grid.setLoading(false);
    				process.destroy();
    			}
    		}
    	});
    },
    onCheck: function(grid, process, callback) {
    	Ext.Ajax.request({
    		url: basePath + 'system/checkInitData.action',
    		timeout: 300000,
    		method: 'post',
    		params: {
    			id: grid.ilid
    		},
    		callback: function(opt, s, r){
    			var rs = Ext.decode(r.responseText);
    			if(!rs || rs.exceptionInfo) {
    				grid.setLoading(false);
    				process.destroy();
    				Ext.Msg.alert('发现错误', (rs ? rs.exceptionInfo : '连接超时'));return;
    			} else {
    				process.updateProgress(0.4, '准备完毕，正在校验...', true);
    				callback.call();
    			}
    		}
    	});
    },
    afterCheck: function(grid, process, callback) {
    	Ext.Ajax.request({
    		url: basePath + 'system/afterCheckLog.action',
    		timeout: 300000,
    		params: {
    			id: grid.ilid
    		},
    		method: 'post',
    		callback: function(opt, s, r){
    			var res = Ext.decode(r.responseText);
    			if(!res) {
    				grid.setLoading(false);
    				p.destroy();
    				Ext.Msg.alert('发现错误', '连接超时');
    			} else if(res.success) {
    				process.updateProgress(0.8, '校验完成，正在获取校验结果...', true);
    				callback.call();
    			}
    		}
		});
    },
    /**
     * 从数据库取校验结果
     */
    getCheckResult: function(grid, p){
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'system/getCheckResult.action',
    		timeout: 60000,
    		params: {
    			id: grid.ilid
    		},
    		method: 'post',
    		callback: function(opt, s, r){
    			p.destroy();
    			grid.setLoading(false);
    			var rs = r.responseText;
    			if(rs != null && rs != '') {
    				grid.errorNodesStr = rs;
    				if(rs==' '){
    					Ext.getCmp('toformal').show();
    				}else{
    					Ext.getCmp('errdelete').show();
    				}
    				grid.errorNodes = rs.replace(/\s/g, '').split(',').filter(function(a){
    					console.log(a);
    					return !!a;
    				});
    			} else {
    				grid.errorNodes = [];
    				alert('系统错误，未找到校验结果.');
    			}
    			grid.showErrors();
    	    	//Ext.getCmp('saveupdates').show();
    		}
    	});
    },
    toformal: function(grid){
    	var count = grid.down('pagingtoolbar').dataCount, pageSize = 2000, page = 0, a, b,
			len = Math.ceil(count/pageSize), index = 0,me=this;
			pageSize = count;
			len = 1;
		var p = Ext.create('Ext.ProgressBar', {
			width: '60%',
			text: '准备中...',
			floating: true,
			renderTo: Ext.getBody()
		}).show();
		grid.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'system/beforeToFormal.action',
			params: {
				id: grid.ilid
			},
			callback: function(opt, s, r){
				var res = Ext.decode(r.responseText);
				if (!s) {
					grid.setLoading(false);
					p.destroy();
					showError(res.exceptionInfo);return;
				}
				if(res.success) {
					while (page*pageSize < count) {
						a = page * pageSize + 1;
						b = (page + 1) * pageSize;
						p.updateProgress(index/len, '开始导入' + index/len*100 + '%', true);
						Ext.Ajax.request({
				    		url: basePath + 'salary/saveSalary.action',
				    		timeout: 30000,
				    		method: 'post',
				    		async: false,
				    		params: {
				    			id: grid.ilid,
				    			start: a,
				    			end: b,
				    			date:grid.date,
				    			type:grid.type,
				    		},
				    		callback: function(_opt, _s, _r){
				    			var rs = Ext.decode(_r.responseText);
				    			if(rs.exceptionInfo) {
				    				grid.setLoading(false);
				    				p.destroy();
				    				showError(rs.exceptionInfo);return;
				    			} else if(_s){
				    				index++;
	    			    			p.updateProgress(index/len, '完成' + index/len*100 + '%', true);
	    			    			if(index == len) {
	    			    				grid.setLoading(false);
	    			    				p.destroy();
	    			    				alert('转入成功!');
	    			    				Ext.Ajax.request({
	    			    		    		url: basePath + 'system/afterToFormal.action',
	    			    		    		params: {
	    			    		    			id: grid.ilid
	    			    		    		},
	    			    		    		callback: function(opts,suc,res){
	    			    		    			var rs=Ext.decode(res.responseText);
	    			    		    			if(rs.success){
	    			    		    				Ext.getCmp("alldownload").show();
	    			    		    				Ext.getCmp("la-send").addCls("la-active");
	    			    		    				me.createSendPanel(grid);
	    			    		    			}
	    			    		    		}
	    			    				});
	    			    			}
				    			} else
				    				return;
				    		}
				    	});
						page++;
					}
				} else {
					grid.setLoading(false);
					p.destroy();
					showError(res.exceptionInfo);
				}
			}
		});
    },
    createSendPanel:function(grid){
    	var me=this;
    	var win=Ext.create("Ext.Window",{
    		width:550,
    		height:480,
    		autoShow: true,
    		closeAction:"hide",
    		layout:'border',
    		bodyStyle:{
    			background:"white",
    		},
    		items:[{
    			xtype:"panel",
    			region:"west",
    			frame:true,	
    			width:270,
        		height:480,
    			bodyCls:"background-image",
    		},{
    			xtype:"form",
    			frame:true,
    			region:"center",
    			bodyStyle:{
        			background:"white",
        		},
        		items:[{
        			xtype:'checkboxfield',
        			boxLabel:'开启手写签名',
        			boxLabelCls:"la-checkfield",
        			height:25,
        			width:200,   			
        			checked:true,
        			id:"btn-check",
        			listeners:{
        				afterrender:function(c){
        					var box = c;
        				}
        			}
        		},{
        			xtype:"textareafield",
        			height:260,
        			width:230,
        			emptyText:'添加工资条公告...',
        			margin:"10 0 10 10",
        			id:"btn-text",
        		},{
        			xtype:'button',
        			iconCls: 'x-data-sendnow',
        			text:"立即发送",
        			height:30,
        			width:80,
        			margin:"10 0 10 20",
        			handler:function(btn){
        				var text=Ext.getCmp('btn-text').value, d=Ext.getCmp('template-data').date,signature=Ext.getCmp('btn-check').value,s;
        				if(d){
        					d=d+'';
        					s=d.substring(0,4)+'年'+d.substring(4,6)+'月';
        				}
        				Ext.Ajax.request({
        					url:basePath+'salary/sendMsg.action',
        					method:"post",
        					params:{
        						ilid:grid.ilid,
        						text:text	,
        						date:s,
        						signature:signature?1:0,
        					},
        					callback:function(opts,suc,res){
        						var r=Ext.decode(res.responseText);
        						if(r.success){
        							alert("推送消息已发送!");
        							btn.ownerCt.ownerCt.hide();
        							url="jsps/salary/salaryHistory.jsp?whoami=salaryHistory&date="+grid.date;
        							me.FormUtil.onAdd(null,'历史工资条',url);
        						    return;
        						}
        					}
        				});
        			}
        		},{
        			xtype:'button',
        			iconCls: 'x-data-sendontime',
        			text:"定时发送",
        			height:30,
        			width:80,
        			margin:"10 0 10 60",
        			listeners:{
        				click:function(button){
        					var dw=me.dateWin;
            				if(!dw){
            					dw=me.dateWin=Ext.create("Ext.Window",{
                 					width:200,
                 		    		height:120,
                 		    		autoShow: true,
                 		    		title:"预约发送时间",
                 		    		closeAction:"close",
                 		    		layout:'border',
                 		    		bodyStyle:{
                 		    			background:"white",
                 		    		},
                 		    		buttonAlign:"center",
                 		    		items:[{
                 		    			xtype:'form',
                 		    			frame:true,
                 		    			region:"center",
                 		    			bodyStyle:{
                     		    			background:"white",
                     		    		},
                 		    			items:[{
                 		    				xtype:"datetimefield",
                 		    				margin:"10 5 0 10",
                 		    				id:"appointment",           		    				
                 		    			}]
                 		    		}],
                 		    		buttons:[
                 		    		         {
                 		    		        	 text:"取消",
                 		    		        	 handler:function(btn){
                 		    		        		 btn.ownerCt.ownerCt.close();
                 		    		        	 }
                 		    		        	 
                 		    		         },{
                 		    		        	 text:"确认",
                 		    		        	 handler:function(btn){
                 		    		        		 var time=Ext.getCmp('appointment').value;
                 		    		        		 var g=Ext.getCmp("template-data"),ilid;
                 		    		        		 var text=Ext.getCmp('btn-text').value;
                 		    		        		 var signature=Ext.getCmp('btn-check').value;                		    		        		
                 		    		        		 ilid=g.ilid;
                 		    		        		 if(time&&ilid){
                 		    		        			 var d=new Date(time);
                 		    		        			 var str=d.getFullYear()+'-'+(d.getMonth()+1)+'-'+d.getDate()+' '+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds();
                 		    		        			Ext.Ajax.request({
                 		    	        					url:basePath+'salary/saveDate.action',
                 		    	        					method:"post",
                 		    	        					params:{
                 		    	        						ilid:g.ilid,
                 		    	        						date:str,
                 		    	        						text:text	,
                 		    	        						signature:signature?1:0,
                 		    	        					},
                 		    	        					callback:function(opts,suc,res){
                 		    	        						var r=Ext.decode(res.responseText);
                 		    	        						if(r.success){
                 		    	        							alert("推送消息会在 "+str+" 时间发送!");
                 		    	        							btn.ownerCt.ownerCt.hide();
                 		    	        							button.ownerCt.ownerCt.hide();
                 		    	        							url="jsps/salary/salaryHistory.jsp?whoami=salaryHistory&date="+grid.date;
                 		    	        							me.FormUtil.onAdd(null,'历史工资条',url);
                 		    	        						    return;
                 		    	        						}
                 		    	        					}
                 		    	        				}); 
                 		    		        		 }
                 		    		        	 }
                 		    		         }]
                 				});
            				}         				
            				dw.show();
            			}
        			}
        			
        		}]
    		}]	
    	});
    	win.show();
    },
    /**
     * 保存已修改的数据
     */
    saveUpdates: function(grid){
    	var items = grid.store.data.items,arr = new Array(), d;
    	Ext.each(items, function(item){
    		if(item.dirty) {
    			d = item.data;
    			delete d.log;
    			arr.push(d);
    		}
    	});
    	Ext.Ajax.request({
    		url: basePath + 'system/updateInitData.action',
    		method: 'post',
    		params: {
    			data: Ext.encode(arr)
    		},
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText);
    			if(res.success) {
    				alert('保存成功!');
    				grid.getGridData(1);
    			}
    		}
    	});
    },
    /**
     * 删除校验错误数据
     */
    deleteErrors: function(grid,b) {
    	Ext.Msg.alert('提示', '确定删除?', function(btn){
    		if(btn == 'ok') {
    			Ext.Ajax.request({
    				url: basePath + 'system/init/errdelete.action',
    				params: {
    					id: grid.ilid
    				},
    				callback: function(opt, s, r) {
    					var rs = Ext.decode(r.responseText);
    					if(rs.success) {
    						showMessage('提示','删除成功!');
    						b.hide();
    						if(grid.store.totalCount>0){
    							Ext.get('toformal').show();				
    						}
    						grid.getGridData(1);
    					}
    				}
    			});
    		}
    	});
    },
    parseLogic: function(logic) {
    	if(logic != null) {
    		var gc = logic.split(';'), str = '';
    		for(var i in gc) {
    			var s = gc[i];
    			if(s != null) {
    				if(s.indexOf('unique') > -1) {
    					str += '唯一性;';
    				} else if(s.indexOf('trim') > -1) {
    					str += '不能包含' + s.replace('trim', '') + ';';
    				} else if(s.indexOf('combo') > -1) {
    					str += '只能是' + s.replace('combo', '') + '之一;';
    				} else if(s.indexOf('accord') > -1) {
    					str += '必须存在于' + s.replace('accord', '') + '中;';
    				}else if(s.indexOf('combine') > -1) {
    					str += '组合字段在关联表中不存在;';
    				} else if(s.indexOf('diffence') > -1) {
    					str += '必须与' + s.replace('diffence', '') + '不同;';
    				}else if(s.indexOf('minValue') > -1) {
    					str += '必须大于' + s.replace('minValue', '') + ';';
    				}
    			}
    		}
    		return str;
    	}
    	return null;
    }
});
