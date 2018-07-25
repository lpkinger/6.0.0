Ext.define('erp.view.common.DeskTop.InfoPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet',
	title: '<div class="div-left">通知公告：</div><button class="fa-tab-btn fa-tab-btn-active">内部通知</button><button class="fa-tab-btn">行政公告</button><!-- <button class="fa-tab-btn">时事新闻</button> -->',
	iconCls: 'main-msg',
	enableTools:true,
	alias: 'widget.infoportal',
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
				items:[me._inform(),me._notice()
//				,me._news()
				]
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
	gridConfig:function(c){
		return Ext.apply(c,{
			autoScroll:false,
			columns:[{
				text:'主题',
				draggable:false,
				fixed:true,
				cls:'x-grid-header-simple',
				flex:1,
				dataIndex:'NO_TITLE',
				renderer: function(val, meta, record){							
					return Ext.String.format('<a class="x-btn-link" onclick="openTable({1},\'Note\',\'通知\',\'jsps/oa/info/NoteR.jsp\',\'no_id\',null' + ');" target="_blank">{0}</a>',
							record.get('NO_TITLE'),
							record.get('NO_ID'));					
				}
			},{
				text:'状态',
				draggable:false,
				cls:'x-grid-header-simple',
				width:50,
				dataIndex:'STATUS',
				fixed:true,
				renderer: function readstatus(val,meta,record){
					if(val==-1)return '<span style="color:green">已读</span>';
					else return '<span style="color:red;">未读</span>';
				}
			},{
				text:'发送人',
				cls:'x-grid-header-simple',
				draggable:false,
				fixed:true,
				width:100,
				dataIndex:'NO_APPROVER'
			},{
				text:'发送时间',
				cls:'x-grid-header-simple',
				draggable:false,
				fixed:true,
				width:150,
				dataIndex:'NO_APPTIME',
				xtype:'datecolumn',
			    renderer:function(value){
			      return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
			    }
			}]
		});
	},
	_inform:function(config){
		var me=this;
		return Ext.widget('gridpanel',me.gridConfig({
			title:'内部通知',
			autoScroll:false,
			listeners:{
				activate:function(grid){
					grid.getStore().load({
						params:{
							count:grid.ownerCt.pageCount
						}
					});
				}
			},
			store:Ext.create('Ext.data.Store',{
				fields:['NO_ID','NO_TITLE','NO_APPROVER','NO_APPTIME','STATUS'],
				proxy: {
					type: 'ajax',
					url : basePath + 'common/desktop/note/inform.action',					
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
			})
		}));
	},
	_notice:function(){
		var me=this;
		return Ext.widget('gridpanel',me.gridConfig({
			title:'行政公告',
			autoScroll:false,
			listeners:{
				activate:function(grid){
					grid.getStore().load({
						params:{
							count:grid.ownerCt.pageCount
						}
					});
				}
			},
			store:Ext.create('Ext.data.Store',{
				fields:['NO_ID','NO_TITLE','NO_APPROVER','NO_APPTIME','STATUS'],
				proxy: {
					type: 'ajax',
					url : basePath + 'common/desktop/note/notice.action',
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
			})
		}));
	},
//	_news:function(){
//		var me=this;
//		return Ext.widget('gridpanel',{
//			title:'时事新闻',
//			autoScroll:false,
//			columns:[{
//				text:'主题',
//				draggable:false,
//				fixed:true,
//				cls:'x-grid-header-simple',
//				flex:1,
//				dataIndex:'NE_THEME',
//				renderer: function(val, meta, record){
//					return Ext.String.format('<a href="javascript:openTable({1},null,\'新闻\',\'oa/news/view.action?ne_id={1}&page={2}&isJumpFromIndex={3}\',\'ne_id\',null' + ');" target="_blank">{0}</a>',
//							record.get('NE_THEME'),
//							record.get('NE_ID'),
//							record.get('PAGE'),
//							true);
//				}
//			},{
//				text:'状态',
//				draggable:false,
//				cls:'x-grid-header-simple',
//				width:50,
//				fixed:true,
//				dataIndex:'STATUS',
//				renderer: function readstatus(val,meta,record){
//					if(val==-1)return '<span style="color:green">已读</span>';
//					else return '<span style="color:red;">未读</span>';
//				}
//			},{
//				text:'发送人',
//				draggable:false,
//				cls:'x-grid-header-simple',
//				width:100,
//				fixed:true,
//				dataIndex:'NE_RELEASER'
//			},{
//				text:'发送时间',
//				draggable:false,
//				cls:'x-grid-header-simple',
//				width:150,
//				fixed:true,
//				dataIndex:'NE_RELEASEDATE',
//				xtype:'datecolumn',
//			    renderer:function(value){
//			      return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
//			    }
//			}],
//			listeners:{
//				activate:function(grid){
//					grid.getStore().load({
//						params:{
//							count:grid.ownerCt.pageCount
//						}
//					});
//				}
//			},
//			store:Ext.create('Ext.data.Store',{
//				fields:['NE_THEME','NE_RELEASER','NE_RELEASEDATE','NE_ID','STATUS','PAGE'],
//				proxy: {
//					type: 'ajax',
//					url : basePath + 'common/desktop/news/getNews.action',
//					method : 'GET',
//					extraParams:{
//						count:me.pageCount	
//					},
//					reader: {
//						type: 'json',
//						root: 'data'
//					}
//				}, 
//				autoLoad:false  
//			})
//		});
//	},
	getMore:function(){
		openTable(null,null,'更多消息',"jsps/common/moreinfo.jsp",null,null);				
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