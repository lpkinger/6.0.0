Ext.define('erp.view.oa.doc.DocTabPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.erpDocTabPanel',
	id: 'doctab', 
	collapsible :false,	
	header :false,
	defaults: { 
		autoScroll:true
	},
	activeTab: 0,
	region: 'south',
	tabBar:{
		cls:'bg_tabs'
	},
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	split: true,
	plain:true,
	flex:2,
	minHeight: 80,
	border: false, 
	autoShow: true,
	currentDoc:null,
	currentType:-1,
	powers:{SEE:'dp_see',CONTROL:'dp_control',DOWNLOAD:'dp_download'},
	defaultItems:[{
		title:'目录信息',
		id:0,
		indexId:0,
		DocId:-1,
		groupType:-1,
		layout:'fit',
		items:[{
			xtype:'form',
			id:'folderForm',
			layout:'column',
			autoScroll:true,
			bodyPadding: 5,
			bodyStyle:'background:#fafafa;',
			defaults: {
				anchor: '100%',
				readOnly:true,
				columnWidth:0.33,
				labelStyle:'font-weight: bold; ',
				fieldStyle : 'background:#fafafa;border-bottom:none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom-style:1px solid;border-left:none;'
			},
			defaultType: 'textfield',
			items:[{
				fieldLabel: '目录名称',
				name: 'dl_name',
				value:'目录名称'
			},{
				fieldLabel: '目录',
				name: 'dl_virtualpath'
			},{
				fieldLabel: '文档数',
				name: 'em_code'
			},{
				fieldLabel:'创建时间',
				name:'dl_createtime',
				xtype:'datetimefield',
				format :'Y-m-d H:i:s'
			},{
				fieldLabel:'目录描述',
				name:'dl_remark'
			},{
				fieldLabel:'目录ID',
				name:'dl_id'
			}]

		}]
	}/*,{
		title:'目录订阅',
		id:1,
		indexId:1,
		DocId:-1,
		groupType:-1,
		bodyStyle:'background:#fafafa;',
		items:[{
			xtype:'button',
			text:'订阅本目录',
			cls:'button1 pill',
			style:'margin-left:20px;margin-top:5px;',
			handler:function(btn){
				if(btn.text=='订阅本目录'){
					btn.setText('取消订阅本目录');
				}else btn.setText('订阅本目录');
			}
		},{
			xtype:'button',
			text:'批量订阅',
			cls:'button1 pill',
			style:'margin-left:20px;margin-top:5px;'
		},{ 
			xtype:'textfield',
			labelWidth:150,
			style:'margin-left:20px',
			labelStyle:'font-weight: bold;',
			fieldStyle : 'background:#fafafa;border-bottom:none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom-style:1px solid;border-left:none;',
			fieldLabel:'已订阅本目录的用户',
			value:'无'
		}]
	}*/,{
		title:'权限管理',
		id:2,
		indexId:2,
		DocId:-1,
		groupType:-1,
		layout:'fit',
		listeners:{
			activate:function(tab){
				tab.items.items[0].loadNewStore();
			}
		}, 
		items:[{
			xtype:'gridpanel',
			columns:DOC.columns.PowerColumns,
			powerSet:['dp_see','dp_save','dp_read','dp_delete','dp_print','dp_download','dp_upload'],
			/*powerSet:['dp_control','dp_see','dp_save','dp_read','dp_delete','dp_print','dp_download','dp_upload'],*/
			/*tbar: [{
				xtype:'button',
				text:'设置权限',
				cls:'button1 pill',
				id:'setpower',
				style:'margin-left:20px; margin-top:5px;'
			}],*/
		 	features : [Ext.create('Ext.grid.feature.GroupingSummary',{
		        groupHeaderTpl: '{name} (共:{rows.length}条)'
		    })],
			autoScroll:true,
			region: 'south',
			layout : 'fit',
			plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				clicksToEdit: 1
			})],
			store: Ext.create('Ext.data.Store', {
				//groupField: 'dp_type',
				fields:DOC.fields.PowerFields		
			}),
			loadNewStore:function(){
				var docpanel=Ext.getCmp('doctab');
				docpanel.loadPowerStore(this,{
					caller:'DocumentListPower',
					condition:"dp_dclid="+CurrentFolderId
				});
			}

		}]
	},{
		title:'文档链接',
		id:3,
		indexId:3,
		DocId:-1,
		groupType:-1,
		items:[{
			xtype:'gridpanel',
			columns:DOC.columns.LinkColumns
		}]
	},{
		title:'文档信息',
		groupType:0,
		id:4,
		indexId:4,
		DocId:-1,
		hidden:true,
		layout:'fit',
		bodyStyle: "border-width:0px!important;border-style:none!important;background-color:#fff;",
		defaults:{
			labelAlign:'right'
		},
		items:[{
			xtype:'form',
			id:'docForm',
			layout:'column',
			autoScroll:true,
			//bodyStyle: "border-width:0px!important;border-style:none!important;background-color:#fff;",
			bodyStyle:'background:#fafafa;',
			bodyPadding: 5,
			defaults: {
				anchor: '100%',
				readOnly:true,
				columnWidth:0.33,
				labelWidth:70,
				labelAlign:'right',
				//labelStyle:'font-weight: bold; ',
				//fieldStyle : 'background:#fafafa;border-bottom:none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom-style:1px solid;border-left:none;'			},
				fieldStyle : 'background:#FFFAFA;color:#515151;;background:#f9f9f9;'			},
			listeners :{
				afterrender:function(form){
					var docpanel=Ext.getCmp('doctab');
					var data=docpanel.currentDoc!=null?docpanel.currentDoc:Ext.getCmp('docgrid').getSelectionModel();
					var value = Ext.util.Format.round(docpanel.currentDoc.dl_size / 1024 /1024,2);
					form.getForm().setValues(docpanel.currentDoc);
					Ext.getCmp('dl_size').setValue(value + 'MB');
					//单据链接按钮
					var linked = Ext.getCmp('dl_linked').value;
					if(linked == null || linked == ''){
						Ext.getCmp('dllinkedbtn').setVisible(false);
					}else{
						Ext.getCmp('dllinkedbtn').setVisible(true);
					}
				}
			},
			defaultType: 'textfield',
			items:[{
				fieldLabel: '编号',
				name: 'dl_code'
			},{
				fieldLabel: '文档名称',
				name: 'dl_name'
			},{
				fieldLabel: '目录',
				name: 'dl_virtualpath'
			},{
				xtype: 'container',
				layout:'column',
				items:[{
					name:'dl_version',
					xtype:'textfield',
					labelWidth:70,
					labelStyle:'font-weight: bold; ',
					fieldLabel:'版本号',
					fieldStyle : 'background:#fafafa;border-bottom:none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom-style:1px solid;border-left:none;',
					columnWidth:1,
					readOnly: true
				},{
					xtype:'button',
					id:'updatedoc',
					text:'修改',
					cls:'button1 pill',
					style:'margin-left:5px;',
					columnWidth:0.2,
					hidden:true
				},{
					xtype:'button',
					text:'历史',
					cls:'button1 pill',
					style:'margin-left:5px;',
					columnWidth:0.2,
					hidden:true
				}]

			},{
				fieldLabel:'大小',
				name:'dl_size',
				id: 'dl_size'
			},{
				fieldLabel:'目录描述',
				name:'dl_remark'
			},{
				fieldLabel:'创建人',
				name:'dl_creator'
			},{
				fieldLabel:'创建时间',
				name:'dl_createtime',
				xtype:'datetimefield',
				format :'Y-m-d H:i:s'
			},/*{
				fieldLabel:'当前状态',
				name:'dl_status'
			},*/{
				xtype: 'container',
				layout:'column',
				items:[  {
					name:'dl_locked',
					xtype:'textfield',
					id:'lockstatus',
					labelWidth:70,
					labelStyle:'font-weight: bold; ',
					fieldLabel:'锁定状态',
					fieldStyle : 'background:#fafafa;border-bottom:none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom-style:1px solid;border-left:none;',
					columnWidth:1,
					readOnly: true,
					listeners:{
						change:function(field,newvalue){/*
							//有且仅有一个被选中的时候进行加锁解锁操作
							var docpanel=Ext.getCmp('docpanel');
							var selectList = Ext.getCmp('docgrid').getSelectionModel().getSelection();
	    				    var select =docpanel.currentItem || selectList[0];
	    				    if(selectList.length==1){
	    				    	//Ext.getCmp('docgrid').getSelectionModel().getSelection()[0].data.dl_locked
	    				    	var lockstatus = selectList[0].data.dl_locked;
	    				    	lockstatus=(lockstatus==0 || lockstatus=='未锁定')?'未锁定':'已锁定';
	    				    	var lockdoc=Ext.getCmp('lockdoc'),buttontext=lockstatus=='未锁定'?'锁定':'解锁';
	    				    	if(newvalue){
	    				    		newvalue=(newvalue==0 || newvalue=='未锁定')?'未锁定':'已锁定';
	    				    		field.setValue(newvalue);
	    				    		lockdoc.setText(buttontext);
	    				    	}else{
	    				    		field.setValue(lockstatus);
									lockdoc.setText(buttontext);
	    				    	//}
	    				    }else{
	    				    	newvalue=(newvalue==0 || newvalue=='未锁定')?'未锁定':'已锁定';
								field.setValue(newvalue);
								var lockdoc=Ext.getCmp('lockdoc'),buttontext=newvalue=='未锁定'?'锁定':'解锁';
								lockdoc.setText(buttontext);
	    				    }
							
						*/}
					}
				},{
					xtype:'button',
					text:'锁定',
					id:'lockbutton',
					cls:'button1 pill',
					style:'margin-left:5px;',
					columnWidth:0.2,
					hidden:true
				}]

			},/*{
				fieldLabel:'点击数',
				name:'dl_hits'
			},{
				fieldLabel:'目录ID',
				name:'dl_id'
			},*/{
				fieldLabel:'文档标签',
				name:'dl_labelinfo',
				columnWidth:0.66
			},{
				fieldLabel:'文档来源',
				name:'dl_source',
				id: 'dl_source'
			},{
				fieldLabel:'单据名称',
				name:'dl_displayname',
				id:'dl_displayname'
			},{
				fieldLabel:'单据链接',
				name:'dl_linked',
				id:'dl_linked',
				hidden: true
			},{
				xtype: 'button',
				id:'dllinkedbtn',
				text:'单据链接',
				style:'margin-left:5px; margin-top:7px;',
				columnWidth:0.07,
				listeners:{
					click: function(btn){
						var value = Ext.getCmp('dl_linked').value;
						if(value == "" || value == null){
							Ext.Msg.alert("提示", "无法链接到单据");
							return;
						}
						window.open(basePath + value);
					}
				}
			}]
		}]

	},{
		title:'修订版管理',
		groupType:0,
		id:5,
		indexId:5,
		DocId:-1,
		hidden:true,
		items:[{
			xtype:'gridpanel',
			columnLines : true,
			autoScroll : true,
			columns:DOC.columns.HistoryColumns,
			store:Ext.create('Ext.data.Store', {
				fields:DOC.fields.VersionFields,
				data:[]
			})
		}],
		listeners:{
			activate:function(tab){
				var docpanel=Ext.getCmp('doctab');
				var data=docpanel.currentDoc!=null?docpanel.currentDoc:Ext.getCmp('docgrid').getSelectionModel();
				docpanel.loadNewStore(tab.items.items[0],{
					caller:'DocumentVersion',
					condition:"dv_dlid="+data.dl_id
				});
			}
		}
	}/*,{
		title:'文档评论',
		groupType:0,
		id:6,
		indexId:6,
		DocId:-1,
		hidden:true,
		autoScroll:true,
		listeners:{
			activate:function(tab){
				var docpanel=Ext.getCmp('doctab');
				var data=docpanel.currentDoc!=null?docpanel.currentDoc:Ext.getCmp('docgrid').getSelectionModel();
				docpanel.loadNewStore(tab.items.items[0],{
					caller:'DocumentReview',
					condition:"dr_dlid="+data.dl_id
				});
			}
		},
		items:[{
			xtype:'gridpanel',
			columns:DOC.columns.ReviewColumns,
			store:Ext.create('Ext.data.Store', {
				fields:DOC.fields.ReviewFields,
				data:[]
			})
		},{
			xtype:'form',
			style:'padding-top:15px;',
			autoScroll:true,					
			border: false,
			height:150,
			layout:'column',
			defaults:{
				columnWidth:0.33
			},
			bodyStyle:'background:#fafafa;',
			items:[{
				xtype:'textfield',
				fieldStyle : 'background:#fafafa;border-bottom:none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom-style:1px solid;border-left:none;'
			},{
				xtype: 'textareafield',
				fieldLabel: '<div align="center"  class="WindowTitle">发表评论</div>',
				labelCls:'x-form-item-label x-form-item-label-top',
				name:'talk',
				id:'talk',
				labelSeparator:'',
				style:'padding-left:50px',
				allowBlank: false,
				width:300			
			}],
			buttonAlign:'center',
			buttons:[{
				text:'保存',				
				cls:'button1 pill',
				style:'margin-top:5px;',
				xtype:'button',
				handler:function(btn){
					var doctab=btn.ownerCt.ownerCt.ownerCt;
					var o={
							dr_remark:Ext.getCmp('talk').value,
							dr_dlid:doctab.ownerCt.currentDoc.dl_id
					};						 
					Ext.Ajax.request({
						url : basePath + 'oa/Doc/review.action',
						params : {
							formStore:unescape(Ext.JSON.encode(o).replace(/\\/g,"%"))
						},
						method : 'post',
						callback : function(options,success,response){
							var localJson = new Ext.decode(response.responseText);
							if(localJson.exceptionInfo){
								var str = localJson.exceptionInfo;
								showError(str);
							}else {
								showResult('提示','评论成功!',btn);
								var grid=doctab.items.items[0];
								Ext.getCmp('talk').reset();
								doctab.ownerCt.loadNewStore(grid,{
									caller:'DocumentReview',
									condition:"dr_dlid="+doctab.ownerCt.currentDoc.dl_id
								})
							}
						}
					});
				}
			}]
		}]   
	},{
		title:'文档审计',
		groupType:0,
		id:7,
		indexId:7,
		DocId:-1,
		hidden:true,
		items:[{
			xtype:'gridpanel',
			columns:DOC.columns.LogColumns
		}]
	},{
		title:'摘要及省略图',
		groupType:0,
		id:8,
		indexId:8,
		DocId:-1,
		hidden:true,
		items:[{
			xtype:'button',
			text:'增加文档摘要',
			cls:'button1 pill',
			style:'margin-left:20px;margin-top:10px;',
			handler:function(btn){

			}
		},{
			xtype:'button',
			text:'打印图片',
			cls:'button1 pill',
			style:'margin-left:20px;margin-top:10px;'
		}]
	},{
		title:'关联文档',
		groupType:0,
		id:9,
		indexId:9,
		DocId:-1,
		hidden:true,
		items:[{
			xtype: 'container',
			layout:'column',
			style:'margin-top:5px;background:transparent;',
			bodyStyle:'background:transparent;',
			items:[{
				xtype:'textfield',
				fieldStyle : 'background:transparent;border-bottom:none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom-style:1px solid;border-left:none;',
				columnWidth:0.33
			},{
				name:'code',
				xtype:'textfield',
				labelWidth:70,
				labelStyle:'font-weight: bold; ',
				fieldLabel:'文档编号',
				columnWidth:0.2
			},{
				xtype:'button',
				id:'addrelatedoc',
				text:'增加关联文档',
				cls:'button1 pill',
				style:'margin-left:5px;',
				columnWidth:0.1
			}]

		},{
			xtype:'gridpanel',
			columns:DOC.columns.RelateColumns
		}]
	},{
		title:'借阅情况',
		groupType:0,
		id:10,
		indexId:10,
		DocId:-1,
		hidden:true,
		items:[{
			xtype:'gridpanel',
			columns:DOC.columns.BorrowColumns
		}]
	}*/],
	defaults:{
		active:function(tab){
			var dlid=tab.ownerCt.currentDoc.data.dl_id;
			if(tab.DocId !=tab.ownerCt.currentDoc.data.dl_id){
				//说明切换了
				tab.ownerCt.changeTab();
				tab.DocId=dlid;
			}
		}
	},
	initComponent : function(){
		this.items=this.defaultItems;
		this.addEvents({
			tabItemChange: true
		});
		this.callParent(arguments); 
	},
	listeners:{
		tabItemChange:function(tabpanel,record){
			if(record && record.data !=this.currentDoc){
				this.currentDoc=record.data;
				var kind=record.data.dl_kind ==0?0:-1;
				if(record.data.dl_kind!=this.currentType){
					this.changeItems(record,kind);
				}else  this.changeItems(record);

			}
			
		}		
	},
	changeItems:function(record,type){
		if(type!=null){
			Ext.Array.each(this.items.items,function(item){
				if(item.groupType!=type){
					item.tab.hide();
				}else {
					if(item.title=='权限管理'){
					     var doctab=Ext.getCmp('doctab');
						 var bool=doctab.checkPowerByFolderId(CurrentFolderId,doctab.powers.CONTROL);
						 if(bool) item.tab.show(); else item.tab.hide();
					}else item.tab.show();
				}
			});
			if(this.currentType!=type && this.activeTab.indexId>3)
				this.setActiveTab(this.activeTab.indexId-4);
			else if(this.currentType!=type && this.activeTab.indexId<4)
				this.setActiveTab(this.activeTab.indexId+4);
			else {
				var tab=this.getActiveTab();
				tab.fireEvent('activate', tab);
			}
			this.currentType=type;
		}else {
			if(this.currentType==-1){
				Ext.getCmp('folderForm').getForm().setValues(record.data);
			}else {
				var value = Ext.util.Format.round(record.data.dl_size / 1024 /1024,2);
				Ext.getCmp('docForm').getForm().setValues(record.data);
				Ext.getCmp('dl_size').setValue(value + 'MB');
				var linked = Ext.getCmp('dl_linked').value;
				if(linked == null || linked == ''){
					Ext.getCmp('dllinkedbtn').setVisible(false);
				}else{
					Ext.getCmp('dllinkedbtn').setVisible(true);
				}
			}	
		}      
	},
	loadNewStore: function(grid, param){
		var me = this;
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + "common/loadNewGridStore.action",
			params: param,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				var data = res.data;
				if(!data || data.length == 0){
					grid.store.removeAll();
				} else {
					grid.store.loadData(data);
				}
				//自定义event
				grid.addEvents({
					storeloaded: true
				});
				grid.fireEvent('storeloaded', grid, data);
			}
		});
	},
	loadPowerStore: function(grid, param){
		var me = this;
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + "common/loadNewGridStore.action",
			params: param,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				var data = res.data;
    			Ext.each(res.data, function(item){
    				Ext.each(grid.powerSet, function(pp){
                            item[pp] = item[pp] == 1;
    				});
    			});
				if(!data || data.length == 0){
					grid.store.removeAll();
				} else {
					grid.store.loadData(data);
				}
				//自定义event
				grid.addEvents({
					storeloaded: true
				});
				grid.fireEvent('storeloaded', grid, data);
			}
		});
	},
	checkPowerByFolderId:function(folderId,type){
		var me = this,bool=false;
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + "doc/CheckPowerByFolderId.action",
			params: {
				folderId:folderId,
				type:type
			},
			async:false,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
			    bool=res.bool;
				
			}
		});
		return bool;
	}
	
});