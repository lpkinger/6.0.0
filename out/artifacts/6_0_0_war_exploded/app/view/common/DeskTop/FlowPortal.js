Ext.define('erp.view.common.DeskTop.FlowPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet',
	alias: 'widget.flowportal',
	title: '<div class="div-left">审批流程：</div><button class="fa-tab-btn fa-tab-btn-active">待办</button><button class="fa-tab-btn">待发起</button><button class="fa-tab-btn">已办理</button><button class="fa-tab-btn">已发起</button>',
	iconCls: 'main-todo',
	enableTools:true,
	animCollapse: false,
	pageCount:10,
	activeRefresh:true,
	autoRefresh:true,
	cls:'top_flowportal',
	itemConfig:{
		toDo:'待办',
		toLaunch:'待发起',
		alreadyDo:'已办理',
		alreadyLaunch:'已发起'  
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
	_toDo:function(){
		var me=this,fields=['JP_STATUS','JP_NODEID','JP_NAME','JP_NODENAME','JP_LAUNCHTIME','JP_LAUNCHERNAME','JP_CODEVALUE','JP_PROCESSNOTE','CURRENTMASTER','JP_STATUS','TYPECODE'];    
		return Ext.widget('gridpanel',{
			title:arguments[1],
			layout:'fit',
			columns:[{
				text:'标题',
				cls:'x-grid-header-simple',
				dataIndex:'JP_NODEID',
				flex:1,
				id: 'topic',
				renderer:function(val,meta,record){
					var note=record.get('JP_PROCESSNOTE'),remindImg='',url='jsps/common/flow.jsp',CURRENTMASTER=record.get('CURRENTMASTER'),TYPECODE=record.get('TYPECODE');
					if(note==null || note =='' || note=='null') {
						note='';
					}
					else note='</br><font color="#777">'+note+'</font>';
					if(TYPECODE=='procand'){
						url='jsps/common/jtaketask.jsp';
						remindImg='<img src="'+basePath+'resource/images/mainpage/mail_take.png" data-qtip="待接管">';
					}else if(TYPECODE=='unprocess'){
						url+='?_do=1';
						url += '&_disagree=1';
						remindImg='<img src="'+basePath+'resource/images/mainpage/mail_un.png" data-qtip="未通过">';
					} 
					else remindImg='<img src="'+basePath+'resource/images/mainpage/mail_ok.png" data-qtip="待审批">';
					return Ext.String.format('{5}<span style="color:#436EEE;padding-left:2px"><a class="x-btn-link" onclick="openTable({3},\'JProcess!Me\',\'任务流程\',\'{6}\',\'jp_nodeId\',null,null,\'{4}\');" target="_blank" style="padding-left:2px">{0}&nbsp;{1}</a>{2}</span>',
							record.get('JP_NAME'),
							record.get('JP_CODEVALUE'),
							note,
							record.get('JP_NODEID'),
							CURRENTMASTER,
							remindImg,url
					);
				}
			},{
				text:'发起人',
				cls:'x-grid-header-simple',
				width:80,
				dataIndex:'JP_LAUNCHERNAME'
			},{
				text:'发起时间',
				cls:'x-grid-header-simple',
				width:150,
				dataIndex:'JP_LAUNCHTIME',
				xtype:'datecolumn',
				renderer:function(value){
					return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
				}
			}],
			viewConfig :{
				stripeRows:false,
				trackOver: false,
				plugins: [{
					ptype: 'preview',
					expanded: true,
					pluginId: 'preview'
				}]
			},
			store:me.getQueryStore(fields,arguments[0])
		});
	},
	_toLaunch:function(){
		var me=this,fields=['TITLE','CODE','PAGELINK'];
		return Ext.widget('gridpanel',{
			title:arguments[1],
			columns:[{
				text:'单据编号',
				//id:'topic',
				cls:'x-grid-header-simple',
				flex:0.5,
				dataIndex:'CODE',
				renderer:function(val,meta,record){
					return Ext.String.format('<a href="javascript:openUrl(\'{0}\',null);" target="_blank">{1}</a>',
							record.get('PAGELINK'),
							record.get('CODE')
					);
				}
			},{
				text:'标题',
				cls:'x-grid-header-simple',
				dataIndex:'TITLE',
				flex:0.5
			}],
			store:me.getQueryStore(fields,arguments[0])
		});
	},
	_alreadyDo:function(){
		var me=this,fields=['JP_NAME','JP_STATUS','JP_NODEID','JP_CODEVALUE','JN_NAME','JN_DEALRESULT','JN_DEALTIME','JN_NODEDESCRIPTION'];    
		return Ext.widget('gridpanel',{
			title:arguments[1],
			layout:'fit',
			columns:[{
				text:'标题',
				dataIndex:'JP_NODEID',
				cls:'x-grid-header-simple',
				//id: 'topic',
				flex:1,
				renderer:function(val,meta,record){
					var description=record.get('JN_NODEDESCRIPTION');
					if(description==null || description =='' || description=='null') {
						description='';
					}
					else description='</br><font color="#777">'+description+'</font>';
					return Ext.String.format('<a class="x-btn-link" onclick="openTable({3},\'JProcess!Me\',\'任务流程\',\'jsps/common/flow.jsp?_do=1\',\'jp_nodeId\',null);" target="_blank">{0}&nbsp;{1}</a>{2}',
							record.get('JP_NAME'),
							record.get('JP_CODEVALUE'),
							description,
							record.get('JP_NODEID')
					);
				}
			},{
				text:'处理结果',
				cls:'x-grid-header-simple',
				dataIndex:'JN_DEALRESULT'
			},{
				text:'处理时间',
				dataIndex:'JN_DEALTIME',
				cls:'x-grid-header-simple',
				width:150
			}],
			store:me.getQueryStore(fields,arguments[0])
		});
	},
	_alreadyLaunch:function(){
		var me=this,fields=['JP_STATUS','JP_NODEID','JP_NAME','JP_NODENAME','JP_NODEDEALMANNAME','JP_LAUNCHTIME','JP_LAUNCHERNAME','JP_CODEVALUE'];    
		return Ext.widget('gridpanel',{
			title:arguments[1],
			columns:[{
				text:'标题',
				cls:'x-grid-header-simple',
				flex:1,
				dataIndex:'JP_NAME',
				//id:'topic',
				renderer:function(val,meta,record){
					var description=record.get('JN_NODEDESCRIPTION');
					if(description==null || description =='' || description=='null') {
						description='';
					}
					else description='</br><font color="#777">'+description+'</font>';
					return Ext.String.format('<a class="x-btn-link" onclick="openTable({3},\'JProcess!Me\',\'任务流程\',\'jsps/common/flow.jsp?_do=1\',\'jp_nodeId\',null);" target="_blank">{0}&nbsp;{1}</a>{2}',
							record.get('JP_NAME'),
							record.get('JP_CODEVALUE'),
							description,
							record.get('JP_NODEID')
					);
				}
			},{
				text:'当前节点',
				dataIndex:'JP_NODENAME',
				cls:'x-grid-header-simple',
				flex:0.5
			},{
				text:'状态',
				dataIndex:'JP_STATUS',
				cls:'x-grid-header-simple',
				width:60
			},{
				text:'处理人',
				dataIndex:'JP_NODEDEALMANNAME',
				cls:'x-grid-header-simple',
				width:80
			}],
			store:me.getQueryStore(fields,arguments[0])
		});
	},
	_alreadyCommunicate:function(){
		return Ext.widget('gridpanel',{
			title:'已沟通',
			columns:[{
				text:'标题'
			},{
				text:'沟通节点'
			},{
				text:'状态'
			}]
		});
	},
	getQueryStore:function(fields,type,autoLoad){
		var me=this;
		return Ext.create('Ext.data.Store',{
			fields:fields,
			proxy: {
				type: 'ajax',
				url : basePath + 'common/desktop/process/'+type+'.action',
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
	/*getMore:function(){
		openTable(null,null,'更多流程',"jsps/common/moreflow.jsp",null,null);				
	},*/
	getMore:function(){
		//openTable(null,null,'流程中心',"jsps/common/messageCenter/JProcessCenter.jsp",null,null);
		  
	  	var main = parent.Ext.getCmp("content-panel");
		var panel=parent.Ext.getCmp('jprocesscenter');
		if(!panel){
			var url=basePath+'jsps/common/messageCenter/JProcessCenter.jsp',
	    	panel = { 
	    			title : '流程中心',
	    			id:'jprocesscenter',
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',    	
	    			items: {xtype: 'component',
							id:'iframe_detail_process',									
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