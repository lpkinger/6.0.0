Ext.define('erp.view.common.DeskTop.InfoRemindPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet',
	title: '<div class="div-left">消息提醒</div>',
	iconCls: 'main-msg',
	id:'inforemindportal',
	enableTools:true,
	alias: 'widget.inforemindportal',
	activeRefresh:true,
	initComponent : function(){
		var me=this;
		Ext.apply(this,{
			items:[Ext.widget('tabpanel',{
				autoShow: true, 
				tabPosition:'top',
				minHeight:200,
				frame:true,
				bodyBorder: false,
				border: false,
				items:[me._unread()
				,me._read()
				]
			})]
		});
		this.callParent(arguments);
	},
	gridConfig:function(c){
		var me = this;
		return Ext.apply(c,{
			autoScroll:false,
			columns:[{
				text:'信息详情',
				draggable:false,
				fixed:true,
				cls:'x-grid-header-simple',
				flex:1,
				dataIndex:'IH_CONTEXT',
				renderer: function(v, meta, record){
					return v.replace(/font-size\s*:\s*\d+\s*p[tx]/g,'font-size:14px').replace(/javascript:openUrl\(/g,"javascript:openTmpUrl('',").replace(/javascript:parent.openUrl\(/g,"javascript:openTmpUrl('patrnt',").replace(/openMessageUrl/g,'openTmpMessageUrl');
//					return v.replace(/font-size\s*:\s*\d+\s*p[tx]/g,'font-size:14px').
//							replace(/javascript:openUrl\(/g,"javascript:openTmpUrl('','"+this.title+"',").
//							replace(/javascript:parent.openUrl\(/g,"javascript:openTmpUrl('patrnt',").
//							replace(/openMessageUrl/g,'openTmpMessageUrl');			
				}
			},{
				text:'状态',
				draggable:false,
				cls:'x-grid-header-simple',
				width:50,
				dataIndex:'IHD_READSTATUS',
				fixed:true,
				renderer: function readstatus(val,meta,record){
					if(val==-1)return '<span style="color:green">已读</span>';
					else return '<span style="color:red;">未读</span>';
				}
			},{
				text:'发起人',
				cls:'x-grid-header-simple',
				draggable:false,
				fixed:true,
				width:100,
				dataIndex:'IH_CALL'
			},{
				text:'发起时间',
				cls:'x-grid-header-simple',
				draggable:false,
				fixed:true,
				width:150,
				dataIndex:'IH_DATE',
				xtype:'datecolumn',
			    renderer:function(value){
			      return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    }
			}],
			listeners:{
				cellclick:function(grid ,td,cellIndex,record ,tr,rowIndex,e){
					var myGrid = grid.ownerCt;
					var field = grid.ownerCt.columns[cellIndex].dataIndex;
					var IHD_ID=record.data.IHD_ID;
					var CURRENTMASTER=record.data.CURRENTMASTER;
					var str='{"IHD_ID":'+IHD_ID+',"CURRENTMASTER":\''+CURRENTMASTER+'\'}';
        			myGrid.readStatusData =str;
        			myGrid.currentmaster =CURRENTMASTER;
					if (field == 'IH_CONTEXT') {
						me.showdetail(record,myGrid);
					}
				},
				activate:function(grid){
					grid.getStore().load({
					});
				}		
			}
		});
	},
	_unread:function(){
		var me=this;
		return Ext.widget('gridpanel',me.gridConfig({
			title:'未读',
			autoScroll:false,
			id:'unreadGrid',
			store:Ext.create('Ext.data.Store',{
				fields:['IH_ID','IHD_ID','IHD_READSTATUS',
						'IH_CALL','IHD_RECEIVE','IH_DATE',
						'IH_FROM','IH_CONTEXT','IHD_READTIME',
						'RN','CURRENTMASTER'],
				proxy: {
					type: 'ajax',
					url : basePath + 'common/getMessageData.action',					
					method : 'post',	
					extraParams:{
						page:1,
						limit:me.pageCount,
						condition:'IHD_RECEIVEID ='+em_id+' and IHD_READSTATUS =0'	
					},
					reader: {
						type: 'json',
						root: 'data'
					}
				}, 
				autoLoad:true  
			})
		}));
	},
	_read:function(){
		var me=this;
		return Ext.widget('gridpanel',me.gridConfig({
			title:'已读',
			autoScroll:true,
			id:'readGrid',
			store:Ext.create('Ext.data.Store',{
				fields:['IH_ID','IHD_ID','IHD_READSTATUS',
						'IH_CALL','IHD_RECEIVE','IH_DATE',
						'IH_FROM','IH_CONTEXT','IHD_READTIME',
						'RN','CURRENTMASTER'],
				proxy: {
					type: 'ajax',
					url : basePath + 'common/getMessageData.action',					
					method : 'post',	
					extraParams:{
						page:1,
						limit:me.pageCount,
						condition:'IHD_RECEIVEID ='+em_id+' and IHD_READSTATUS =-1'	
					},
					reader: {
						type: 'json',
						root: 'data'
					}
				}, 
				autoLoad:true  
			})
		}));
	},
	setPageCount:function(type,c,o){
		if(type=='ok'){
			if(Ext.isNumeric(c)){
				var portal=o.sourcePortal,itemPortal=portal.items.items[0];
				c = Math.round(c); //四舍五入
				if(portal && portal.maxCount>0 && (c>portal.maxCount || c<1 )){
					alert('记录数超出限制'); return;
				}else {
					Ext.Ajax.request({
						url:basePath+'/common/desktop/setTotalCount.action',
						method:'POST',
						params:{
							type:o.updateXtype,
							count:c
						},
						callback : function(options, success, response){
							var res = response.responseText;
							if(res=='success'){
								//更新成功
								portal.pageCount=c;
								if(itemPortal.xtype=='tabpanel'){
									var store=itemPortal.getActiveTab().getStore();
									var params=store.proxy.extraParams;
									Ext.apply(params,{
										page: 1,
										limit:c
									});
									store.load();
								}else{
									var store=itemPortal.getStore();
									var params=store.proxy.extraParams;
									Ext.apply(params,{
										page: 1,
										limit:c
									});
									store.load();
								}						 
							}					  
						}
					});	
				}
			}else alert('请输入正确的数字!');
		}
	},
    showdetail:function(record,myGrid){
		var me=this;
		var data=record.data.IH_DATE;
		var from=record.data.IH_FROM;
		var context=record.data.IH_CONTEXT;
		var IHD_ID=record.data.IHD_ID;
		var CURRENTMASTER=record.data.CURRENTMASTER;
		var call=record.data.IH_CALL;   		
		var str='{"IHD_ID":'+IHD_ID+',"CURRENTMASTER":\''+CURRENTMASTER+'\'}';
		var windows= Ext.create('Ext.window.Window', {   
	 		x: Ext.getBody().getWidth()/2-200, 
			y: Ext.getBody().getHeight()/2-200,
	 		width:500,
	 		modal:true,
	 		id:'infoRemindwindow',
	 		CURRENTMASTER:CURRENTMASTER,
	 		closable:false,     	
	 		border: false,
	 		frame:false,
	 		resizable :false,
	 		header: false,
	 		draggable: true,
	 		cls:'Windetail',
			items:[
				{
					xtype : 'tbtext',
					text : '<div style="text-align:center;color:#000000;font-weight:normal;font-size: 20px;">信息详情</div>',
					cls:'Wintitle'
				},
				{
				xtype:'panel',
				buttonAlign :'center',
				id:'paneldetail',
				border:false,
				layout:'fit',				
				items:[
					{	
						xtype : 'displayfield',
						fieldLabel: '发起人',
						value:call,
						labelAlign:'left',
						labelWidth:70,
						cls:'Wincontext'
					},
					{		
						xtype : 'displayfield',
						fieldLabel: '发起时间',
						value:data,
						labelAlign:'left',
						labelWidth:70,
						cls:'Wincontext'						
					},
					{
						xtype : 'displayfield',
						cls:'Wincontext',
						labelAlign:'left',
						labelWidth:70,
						fieldLabel: '消息分类',
						value:from,
						renderer: function (v) {
	    				switch(v){
	    					case 'system':
	    						return '知会消息';
	    						break;
	    					case 'crm':
	    						return 'CRM提醒';
	    						break;
	    					case 'note':
	    						return '通知公告';
	    						break;
	    					case 'kpi':
	    						return '考勤提醒';
	    						break;
	    					case 'meeting':
	    						return '会议';
	    						break;
	    					case 'process':
	    						return '审批';
	    						break;
	    					case 'task':
	    						return '任务';
	    						break;
	    					case 'job':
	    						return '稽核提醒';
	    						break;
	    					case 'b2b':
	    						return 'B2B提醒';
	    						break;
	    					case 'ptzh':
	    						return '普通知会';
	    						break;	
	    				}
	    			}},
					{
						xtype : 'displayfield',
						fieldLabel: '内容',
						labelAlign:'left',
						cls:'Wincontext',
	    				name: '',
	    				labelWidth:70,
	    				value:context ,
						renderer: function (v) {
							return '<div style="height:150px;width:336px;margin:0;text-align:left;border:1px solid #A3A3A3;">'+
							context.replace(/javascript:openUrl\(/g,"javascript:openTmpUrl('',").replace(/javascript:parent.openUrl\(/g,"javascript:openTmpUrl('patrnt',").replace(/openMessageUrl/g,'openTmpMessageUrl')+
							'</div>' ;
						}
					}],
		buttons:[{
					xtype:'button',
					text:'<div style="color: white !important">确定</div>',
					align:'center',
					height:30,
					cls:'readbutton',			
					handler:function(v){
						var window = Ext.getCmp('infoRemindwindow');
						if (record.data.IHD_READSTATUS ==0) {
							me.updateReadstatus(str,myGrid);
						}
						window.close();
					}		
				}]							
			}]		
		 });
		windows.show();		 
    },
	updateReadstatus:function(data,myGrid){
       var me = this;
	   Ext.Ajax.request({
            url: basePath + "common/updateReadstatus.action",
            params: {
                data: data     
            },
            method: 'post',
            asyn:false,
            callback: function(options, success, response) {
                var res = Ext.decode(response.responseText); 
        		if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}				
                if (res.success){
                	var grid = me.down('grid');
                	myGrid.store.load();
                }
           }
        });
    },
	getMore:function(){
		openTable(null,null,'更多消息',"jsps/common/messageCenter/information.jsp",null,null);				
	},
	_dorefresh:function(panel){
		var activeTab=panel.down('tabpanel').getActiveTab();
		if(activeTab) {
			//解决刷新时 panel丢失高度 导致panel显示出错
			if(!activeTab._firstWidth){
				if(activeTab.preLayoutSize.width==0){
					activeTab._firstWidth = activeTab.ownerCt.preLayoutSize.width
				}else{
					activeTab._firstWidth = activeTab.preLayoutSize.width
				}
			}
			if(activeTab._firstWidth!=activeTab.preLayoutSize.width){
				activeTab.setWidth(activeTab._firstWidth);
			}
			activeTab.fireEvent('activate',activeTab);
		}
	}
});