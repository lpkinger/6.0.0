Ext.define('erp.view.common.DeskTop.InvitePortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet',
	title: '<div class="div-left">邀请信息</div><button class="fa-tab-btn fa-tab-btn-active">未注册记录</button><button class="fa-tab-btn">邀请记录</button><button class="fa-tab-btn">注册记录</button>',
	iconCls: 'main-msg',
	enableTools:true,
	id:'invitePortal',
	alias: 'widget.invitePortal',
	animCollapse: false,
	pageCount:10,
	activeRefresh:true,
	itemConfig:{
		unReigste:'未注册记录',
		inviteRecords:'邀请记录',
		reigsteRocords:'注册记录'
	},
	initComponent : function(){
		var me=this;
		Ext.apply(this,{
			items:[Ext.widget('tabpanel',{
				id:"inviteTab",
				autoShow: true, 
				tabPosition:'top',
				minHeight:200,
				frame:true,
				bodyBorder: false,
				border: false,
				items:me._initItems()
			})]
		});
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function() {
			var me = this;
			var buttons = me.el.dom.getElementsByClassName('fa-tab-btn');
			Ext.Array.each(buttons, function(btn, i) {
				btn.onclick = function(){
					me.el.dom.getElementsByClassName('fa-tab-btn-active')[0].classList.remove('fa-tab-btn-active');
					this.classList.add('fa-tab-btn-active')
					me.tabChange(i);
				}
			});
		}
	},
	_initItems:function(){
		var me=this,items=new Array(),conf=me.itemConfig;
		for(var c in conf){
			var component=me['_'+c].apply(me,[c,conf[c]]);
			component.addListener('activate',function(c){
				c.getStore().load();
			});
			items.push(component);
		}
		return items;
	},
	_unReigste:function(){
		var me = this,
		fields = ['vendname','vendusername','active','date'];
		return Ext.widget('gridpanel',{
			title:'未注册记录',
			layout:'fit',
			forceFit: true,
			columns:[{
				text:'邀请企业',
				cls:'x-grid-header-simple',
				dataIndex:'vendname',
				width:200
			},{
				text:'联系人',
				cls:'x-grid-header-simple',
				dataIndex:'vendusername',
				width:100
			},{
				text:'注册状态',
				cls:'x-grid-header-simple',
				dataIndex:'active',
				width:100,
				renderer: function (val,meta,record){
					if(val==1)return '<span >已注册</span>';
					else return '<span style="color:red;">未注册</span>';
				}
			},{
				text:'邀请时间',
				cls:'x-grid-header-simple',
				dataIndex:'date',
				xtype:'datecolumn',
				width:150,
			    renderer:function(value){
			      return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    }
			}],
			store:me.getQueryStore(fields,"todo")
		});
	},
	_inviteRecords:function(){
		var me = this,
		fields = ['vendname','vendusername','vendusertel','date','active','registerDate'];
		return Ext.widget('gridpanel',{
			title:'邀请记录',
			layout:'fit',
			forceFit: true,
			columns:[{
				text:'邀请企业',
				cls:'x-grid-header-simple',
				dataIndex:'vendname',
				width:200
			},{
				text:'联系人',
				cls:'x-grid-header-simple',
				dataIndex:'vendusername',
				width:70
			},{
				text:'联系电话',
				cls:'x-grid-header-simple',
				dataIndex:'vendusertel',
				width:100
			},{
				text:'邀请时间',
				cls:'x-grid-header-simple',
				dataIndex:'date',
				xtype:'datecolumn',
				width:150,
			    renderer:function(value){
			      return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    }
			},{
				text:'注册状态',
				cls:'x-grid-header-simple',
				dataIndex:'active',
				width:70,
				renderer: function (val,meta,record){
					if(val==1)return '<span>已注册</span>';
					else return '<span style="color:red;">未注册</span>';
				}
			},{
				text:'注册时间',
				cls:'x-grid-header-simple',
				dataIndex:'registerDate',
				width:150,
				xtype:'datecolumn',
			    renderer:function(value){
			      if(value){
			    		return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    	}else{
			    		return "";
			    	}
			    }
			}],
			store:me.getQueryStore(fields,"")
		});
	},
	_reigsteRocords:function(){
		var me = this,
		fields = ['user','vendname','vendusername','vendusertel','date','active','registerDate'];
		return Ext.widget('gridpanel',{
			title:'注册记录',
			layout:'fit',
			forceFit: true,
			columns:[{
				text:'邀请人',
				cls:'x-grid-header-simple',
				dataIndex:'user',
				width:70,
				renderer:function(value){
					if(value){
						var user = Ext.decode(value);
						return user.userName
					}else{
						return "";
					}
					
				}
			},{
				text:'邀请企业',
				cls:'x-grid-header-simple',
				dataIndex:'vendname',
				width:200
			},{
				text:'联系人',
				cls:'x-grid-header-simple',
				dataIndex:'vendusername',
				width:70
			},{
				text:'联系电话',
				cls:'x-grid-header-simple',
				dataIndex:'vendusertel',
				width:100
			},{
				text:'邀请时间',
				cls:'x-grid-header-simple',
				dataIndex:'date',
				xtype:'datecolumn',
				width:140,
			    renderer:function(value){
			      return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    }
			},{
				text:'注册状态',
				cls:'x-grid-header-simple',
				dataIndex:'active',
				width:70,
				renderer: function (val,meta,record){
					if(val==1)return '<span ">已注册</span>';
					else return '<span style="color:red;">未注册</span>';
				}
			},{
				text:'注册时间',
				cls:'x-grid-header-simple',
				dataIndex:'registerDate',
				xtype:'datecolumn',
				width:140,
			    renderer:function(value){
			    	if(value){
			    		return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    	}else{
			    		return "";
			    	}
				}
			}],
			store:me.getQueryStore(fields,"done")
		});
	},
	getQueryStore:function(fields,_state){
		var me=this;
		return Ext.create('Ext.data.Store',{
					fields:fields,
					pageSize : me.pageCount,
					proxy: {
						type: 'ajax',
						url : basePath+'ac/invitationsRecord.action',
						method : 'GET',
						extraParams:{
							_state:_state,
							count:me.pageCount
						},
						reader: {
							type: 'json',
							root: 'data'
						}
					}, 
					autoLoad:false  
				});
	},
	getMore:function(){
		openTable(null,null,'更多信息',"jsps/scm/purchase/moreInviteInfo.jsp",null,null);				
	},
	_dorefresh:function(panel){
		var activeTab=panel.down('tabpanel').getActiveTab();
		if(activeTab) {
			//解决刷新时 panel丢失高度 导致panel显示出错
			if(!activeTab._firstWidth){
				activeTab._firstWidth = activeTab.preLayoutSize.width
			}
			if(activeTab._firstWidth!=activeTab.preLayoutSize.width){
				activeTab.setWidth(activeTab._firstWidth);
			}
			activeTab.fireEvent('activate',activeTab);
		}
	},
	tabChange: function(index) {
		this.down('tabpanel').setActiveTab(index);
	}
});