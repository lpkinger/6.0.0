Ext.define('erp.view.scm.purchase.moreInviteInfo.Viewport',{ 
	extend: 'Ext.Viewport', 
	id:'moreInviteInfoViews',
	hideBorders: true, 
	itemConfig:{
		unReigste:'未注册记录', // todo
		inviteRecords:'邀请记录', // all
		reigsteRocords:'注册记录' // done
	},
	activeRefresh:true,
	pageCount:pageSize,
	initComponent : function(){ 
		var me = this; 
		Ext.applyIf(me, {  
			xtype : 'panel',
			layout : 'border',
			items:[{
					xtype : 'form',
					layout : 'column',
					region: 'north',
					id:'searchForm',
					height: 50,
					bodyStyle:"padding:10px",
					items:[{
						xtype : 'textfield',
						cls : 'test',
						id : 'keySearch',
						name : 'keySearch',
						emptyText : '输入企业名称等关键词进行搜索',
						region : 'north',
						height : 30,
						width : 500,
						listeners:{
							specialkey : function(field, e){
				        		if(e.getKey() == Ext.EventObject.ENTER&&field&&(!field.itemId)){
				        			var query = Ext.getCmp('query');
				        			query.fireEvent('click',query);
				        		}
				        	}
						}
					},{
						xtype : 'button',
						text : "搜索",
						id:'query',
						cls : 'btn-search',
						listeners:{
							click : function(){
								Ext.getCmp('inviteTab').getActiveTab().getStore().loadPage(1);
							}
						}
					},{ 
					  	xtype: 'button',
					  	text: '邀请注册',
					  	cls : 'btn-inviteVendor',
					  	listeners: {
							click: inviteNew
				    	 }
					}]
			},{
				xtype:'tabpanel',
				id:"inviteTab",
				layout : 'column',
				region: 'center',
				autoShow: true, 
				tabPosition:'top',
				minHeight:200,
				frame:true,
				bodyBorder: false,
				border: false,
				items:me._initItems()
			}]
		});
		me.callParent(arguments); 
	},
	_initItems:function(){
		var me=this,items=new Array(),conf=me.itemConfig;
		for(var c in conf){
			var component=me['_'+c].apply(me,[c,conf[c]]);
			items.push(component);
		}
		return items;
	},
	getQueryStore:function(fields,_state){
		var me=this;
		return Ext.create('Ext.data.Store',{
					fields:fields,
					storeId:_state==null?"allInviteData":_state+"InviteData",
					pageSize : me.pageCount,
					proxy: {
						type: 'ajax',
						url : basePath+'ac/invitationsRecord.action',
						method : 'GET',
						extraParams:{
							_state:_state,
							caller:'invitationsRecord'
						},
						timeout:180000,
						reader: {
							type: 'json',
							root: 'data',
							totalProperty:'count'
						}
					},
					autoLoad:false ,
					listeners : {
						beforeload : function() {
							var inviteTab = Ext.getCmp('inviteTab');
							var grid = inviteTab.getActiveTab();
					    	var keyword = Ext.getCmp('keySearch');
					    	if(keyword){
					    		Ext.apply(grid.getStore().proxy.extraParams, {
					    			keyword: keyword.value
								});
					    	}
						},
						'datachanged':function(){
							Ext.getCmp('moreInviteInfoViews').resetTabTitle();
						}
					}
				})
	},
	_unReigste:function(){
		var me = this;
		var fields = ['vendname','vendusername','active','date'];
		return Ext.widget('InformationGrid',{
			title:'未注册记录',
			id:'todoGrid',
			_state:'todo',
			myfields : ['vendname','vendusername','active','date'],
			columns:[{
				header : '邀请企业',
				text:'邀请企业',
				cls:'x-grid-header-simple',
				dataIndex:'vendname',
				width:200
			},{
				header : '联系人',
				text:'联系人',
				cls:'x-grid-header-simple',
				dataIndex:'vendusername'
			},{
				text:'注册状态',
				cls:'x-grid-header-simple',
				dataIndex:'active',
				renderer: function (val,meta,record){
					if(val==1)return '<span>已注册</span>';
					else return '<span style="color:red;">未注册</span>';
				}
			},{
				text:'邀请时间',
				cls:'x-grid-header-simple',
				dataIndex:'date',
				xtype:'datecolumn',
			    renderer:function(value){
			      return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    }
			}]
		});
	},
	_inviteRecords:function(){
		var me = this;
		return Ext.widget('InformationGrid',{
			title:'邀请记录',
			id:'allGrid',
			_state:'all',
			myfields : ['vendname','vendusername','vendusertel','date','active','registerDate'],
			columns:[{
				text:'邀请企业',
				cls:'x-grid-header-simple',
				dataIndex:'vendname',
				width:200
			},{
				text:'联系人',
				cls:'x-grid-header-simple',
				dataIndex:'vendusername'
			},{
				text:'联系电话',
				cls:'x-grid-header-simple',
				dataIndex:'vendusertel'
			},{
				text:'邀请时间',
				cls:'x-grid-header-simple',
				dataIndex:'date',
				xtype:'datecolumn',
			    renderer:function(value){
			      return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    }
			},{
				text:'注册状态',
				cls:'x-grid-header-simple',
				dataIndex:'active',
				renderer: function (val,meta,record){
					if(val==1)return '<span>已注册</span>';
					else return '<span style="color:red;">未注册</span>';
				}
			},{
				text:'注册时间',
				cls:'x-grid-header-simple',
				dataIndex:'registerDate',
				xtype:'datecolumn',
			    renderer:function(value){
			      if(value){
			    		return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    	}else{
			    		return "";
			    	}
			    }
			}]
		});
	},
	_reigsteRocords:function(){
		var me = this;
		return Ext.widget('InformationGrid',{
			title:'注册记录',
			id:'doneGrid',
			_state:'done',
			myfields : ['user','vendname','vendusername','inviteUserName','inviteEnName','productNum','vendusertel','date','active','registerDate'],
			columns:[{
				text:'邀请人',
				cls:'x-grid-header-simple',
				dataIndex:'inviteUserName'
			},{
				text:'邀请企业',
				cls:'x-grid-header-simple',
				dataIndex:'vendname',
				width:200
			},{
				text:'联系人',
				cls:'x-grid-header-simple',
				dataIndex:'vendusername'
			},{
				text:'联系电话',
				cls:'x-grid-header-simple',
				dataIndex:'vendusertel'
			},{
				text:'邀请时间',
				cls:'x-grid-header-simple',
				dataIndex:'date',
				xtype:'datecolumn',
			    renderer:function(value){
			      return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    }
			},{
				text:'上传物料数',
				cls:'x-grid-header-simple',
				dataIndex:'productNum'
			},{
				text:'注册状态',
				cls:'x-grid-header-simple',
				dataIndex:'active',
				renderer: function (val,meta,record){
					if(val==1)return '<span>已注册</span>';
					else return '<span style="color:red;">未注册</span>';
				}
			},{
				text:'注册时间',
				cls:'x-grid-header-simple',
				dataIndex:'registerDate',
				xtype:'datecolumn',
			    renderer:function(value){
			    	if(value){
			    		return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    	}else{
			    		return "";
			    	}
				}
			}]
		});
	},
	resetTabTitle:function(){
		Ext.Ajax.request({
	   		url : basePath + "ac/invitationCount.action",
	   		method : 'get',
	   		callback : function(options,success,response){
	   			var res = new Ext.decode(response.responseText);
	   			if(res.exceptionInfo){
	   				var str = res.exceptionInfo;			   				
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){			   					
	   					str = str.replace('AFTERSUCCESS', '');	
	   				}
	   				showError(str);return;
	   			}
   				if(res.data){
   					var counts = Ext.decode(res.data);
   					Ext.getCmp("todoGrid").dockedItems.dataCount = counts.todo;
   					Ext.getCmp("allGrid").dockedItems.dataCount = counts.all;
   					Ext.getCmp("doneGrid").dockedItems.dataCount = counts.done;
   					Ext.getCmp("todoGrid").setTitle("未注册记录("+counts.todo+")");
   					Ext.getCmp("allGrid").setTitle("邀请记录("+counts.all+")");
   					Ext.getCmp("doneGrid").setTitle("注册记录("+counts.done+")");
   				}
	   		}
        });
	}
});