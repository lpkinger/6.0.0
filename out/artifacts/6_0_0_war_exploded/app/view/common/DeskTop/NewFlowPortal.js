Ext.define('erp.view.common.DeskTop.NewFlowPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet',
	alias: 'widget.newflowportal',
	title: '<div class="div-left">业务流程：</div><button class="fa-tab-btn fa-tab-btn-active">待处理</button><button class="fa-tab-btn">已办理</button><button class="fa-tab-btn">已发起</button>',
	iconCls: 'main-todo',
	enableTools:true,
	animCollapse: false,
	pageCount:10,
	autoRefresh:true,
	activeRefresh:true,
	itemConfig:{
		pending:'待处理',
		processed:'已处理',
		created:'已发起'  
	},
	initComponent : function(){
		var me=this;
		Ext.apply(this,{
			items:[Ext.widget('tabpanel',{
				autoShow: true, 
				tabPosition:'top',
				minHeight:200,
				frame:true,
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
				c.getStore().load({params:{
					count:me.pageCount
				}});
			});
			items.push(component);
		}
		return items;
	},
	_pending:function(){
		var me=this,fields=['MASTERNAME','FI_ID','FI_FDSHORTNAME','FI_NODEID','FI_CODEVALUE','FI_KEYVALUE','FI_HANDLER','FI_HANDLERCODE','FI_TIME','FI_NODENAME','FI_STARTTIME','FI_STARTMAN',
							'FI_STATUS','FI_STARTMANCODE','FI_KEYFIELD','FI_CALLER','FIR_TYPE','FD_NAME','FI_TITLE'];    
		return Ext.widget('gridpanel',{
			title:arguments[1],
			layout:'fit',
			columns:[{
				text:'流程',
				cls:'x-grid-header-simple',
				dataIndex:'FD_NAME',
				flex:1,
				renderer:function(val,meta,record){
					var url='jsps/oa/flow/Flow.jsp',mastername=record.get('MASTERNAME');
					return Ext.String.format('<span style="color:#436EEE;padding-left:2px"><a class="x-btn-link" onclick="openTable({0},\'{1}\',\'任务流程\',\'{2}\',\'{3}\',null,null,null,true);" target="_blank" style="padding-left:2px">{4}</a></span>',
							record.get('FI_KEYVALUE'),
							record.get('FI_CALLER'),
							url,
							record.get('FI_KEYFIELD'),
							record.get('FD_NAME'),
							record.get('FI_CODEVALUE')
					);
				}
			},{
				text:'标题',
				cls:'x-grid-header-simple',
				dataIndex:'FI_TITLE',
				width:120
			},{
				text:'节点',
				cls:'x-grid-header-simple',
				dataIndex:'FI_NODENAME',
				width:120
			},{
				text:'发起人',
				cls:'x-grid-header-simple',
				width:60,
				dataIndex:'FI_STARTMAN'
			},{
				text:'发起时间',
				cls:'x-grid-header-simple',
				width:150,
				dataIndex:'FI_TIME',
				xtype:'datecolumn',
				renderer:function(value){
					return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
				}
			}],
			store:me.getQueryStore(fields,arguments[0])
		});
	},
	_processed:function(){
		var me=this,fields=['MASTERNAME','FI_ID','FI_FDSHORTNAME','FI_NODEID','FI_CODEVALUE','FI_KEYVALUE','FI_HANDLER','FI_HANDLERCODE','FI_TIME','FI_NODENAME','FI_STARTTIME','FI_STARTMAN',
							'FI_STATUS','FI_STARTMANCODE','FI_KEYFIELD','FI_CALLER','FIR_MANCODE','FIR_TYPE','FD_NAME','FI_TITLE'];    
		return Ext.widget('gridpanel',{
			title:arguments[1],
			layout:'fit',
			columns:[{
				text:'流程',
				cls:'x-grid-header-simple',
				dataIndex:'FD_NAME',
				flex:1,
				renderer:function(val,meta,record){
					var url='jsps/oa/flow/Flow.jsp?nodeId='+record.get('FI_NODEID'),mastername=record.get('MASTERNAME');
					return Ext.String.format('<span style="color:#436EEE;padding-left:2px"><a class="x-btn-link" onclick="openTable({0},\'{1}\',\'任务流程\',\'{2}\',\'{3}\',null,null,null);" target="_blank" style="padding-left:2px">{4}&nbsp;{5}</a></span>',
							record.get('FI_KEYVALUE'),
							record.get('FI_CALLER'),
							url,
							record.get('FI_KEYFIELD'),
							record.get('FD_NAME'),
							record.get('FI_CODEVALUE')
					);
				}
			},{
				text:'标题',
				cls:'x-grid-header-simple',
				dataIndex:'FI_TITLE',
				width:120
			},{
				text:'节点名称',
				dataIndex:'FI_NODENAME',
				cls:'x-grid-header-simple',
				width:120
			},{
				text:'处理时间',
				dataIndex:'FI_TIME',
				cls:'x-grid-header-simple',
				width:150,
				xtype:'datecolumn',
				renderer:function(value){
					return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
				}
			}],
			store:me.getQueryStore(fields,arguments[0])
		});
	},
	_created:function(){
		var me=this,fields=['MASTERNAME','FI_ID','FI_FDSHORTNAME','FI_NODEID','FI_CODEVALUE','FI_KEYVALUE','FI_HANDLER','FI_HANDLERCODE','FI_TIME','FI_NODENAME','FI_STARTTIME','FI_STARTMAN',
							'FI_STATUS','FI_STARTMANCODE','FI_KEYFIELD','FI_CALLER','FD_NAME','FI_TITLE'];    
		return Ext.widget('gridpanel',{
			title:arguments[1],
			columns:[{
				text:'流程',
				cls:'x-grid-header-simple',
				flex:1,
				dataIndex:'FD_NAME',
				renderer:function(val,meta,record){
					var url='jsps/oa/flow/Flow.jsp?nodeId='+record.get('FI_NODEID'),mastername=record.get('MASTERNAME');
					return Ext.String.format('<span style="color:#436EEE;padding-left:2px"><a class="x-btn-link" onclick="openTable({0},\'{1}\',\'任务流程\',\'{2}\',\'{3}\',null,null,null);" target="_blank" style="padding-left:2px">{4}&nbsp;{5}</a></span>',
							record.get('FI_KEYVALUE'),
							record.get('FI_CALLER'),
							url,
							record.get('FI_KEYFIELD'),
							record.get('FD_NAME'),
							record.get('FI_CODEVALUE')
					);
				}
			},{
				text:'标题',
				cls:'x-grid-header-simple',
				dataIndex:'FI_TITLE',
				width:180
			},{
				text:'发起时间',
				dataIndex:'FI_TIME',
				cls:'x-grid-header-simple',
				width:150,
				xtype:'datecolumn',
				renderer:function(value){
					return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
				}
			}],
			store:me.getQueryStore(fields,arguments[0])
		});
	},
	getQueryStore:function(fields,type,autoLoad){
		var me=this;
		return Ext.create('Ext.data.Store',{
			fields:fields,
			proxy: {
				type: 'ajax',
				url : basePath + 'common/desktop/flow/'+type+'.action',
				method : 'GET',
				extraParams:{
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
	  	var main = parent.Ext.getCmp("content-panel");
		var panel=parent.Ext.getCmp('flowcenter');
		if(!panel){
			var url=basePath+'jsps/oa/flow/FlowCenter.jsp',
	    	panel = { 
	    			title : '流程中心',
	    			id:'flowcenter',
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',    	
	    			items: {xtype: 'component',
							id:'iframe_detail_flow',									
							autoEl: {
									tag: 'iframe',
									style: 'height: 100%; width: 100%; border: none;',
									src: url}
	    				},
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
			var p = main.add(panel); 
			main.setActiveTab(p);
		}else{ 
	    	main.setActiveTab(panel); 
		} 
	},
	_dorefresh:function(panel){
		var activeTab=panel.down('tabpanel').getActiveTab();
		if(activeTab){
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