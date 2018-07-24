Ext.require([
    'Ext.ux.PreviewPlugin'
]);

Ext.define('erp.view.common.DeskTop.FeedbackPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet',
	alias: 'widget.feedbackportal',
	title: '<div class="div-left">系统问题反馈：</div><button class="fa-tab-btn fa-tab-btn-active">待处理</button><button class="fa-tab-btn">已发起</button><button class="fa-tab-btn">被驳回</button>',
	iconCls: 'x-button-icon-install',
	enableTools:true,
	animCollapse: false,
	pageCount:10,
	activeRefresh:true,
	cls:'top_feedbackportal',
	//autoRefresh:true,
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
				items:[me._toDo(),me._alreadyLaunch(),me._reject()]
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
	gridConfig:function(b,c){
		return Ext.apply(c,{
			autoScroll:false,
			viewConfig :{
				stripeRows:false,
				trackOver: false,
				plugins: [{
					ptype: 'preview',
					expanded: true,
					pluginId: 'preview'
				}]
			},
			listeners:{
				activate:function(grid){
					grid.getStore().load({
						params:{
							count:grid.ownerCt.pageCount
						}
					});
				}
			},
			columns:[
				{text:'项目',
				cls:'x-grid-header-simple',
				dataIndex:'FB_CODE',
				fixed:true,
				flex:1,
				renderer:function(val,meta,record){	
					meta.tdCls='x-grid-cell-topic1';
					var detail=record.get('FB_DETAIL');
					detail='</br><font color="#777">'+detail+'</font>';				
					return Ext.String.format('<span style="color:#436EEE;padding-left:2px"><a class="x-btn-link" onclick="openTable({0},\'Feedback\',\'系统问题反馈\',\'jsps/sys/Feedback.jsp?caller=Feedback\',\'fb_id\',null,null,null);" target="_blank" style="padding-left:2px">{1}&nbsp;{2}</a>{3}</span>',
							record.get('FB_ID'),
							record.get('FB_CODE'),			
							record.get('FB_PRJNAME'),	
							detail
					);
				}
			},{
				text:'处理人',
				dataIndex:'EM_NAME',
				cls:'x-grid-header-simple',
				width:b?70:0,
				fixed:true
			},{
				text:'提出人',
				dataIndex:'FB_EMNAME',
				cls:'x-grid-header-simple',
				width:b?0:70,
				fixed:true
			},{
				text:'提出时间',
				cls:'x-grid-header-simple',
				width:150,
				fixed:true,
				dataIndex:'FB_DATE',
				xtype:'datecolumn',
				renderer:function(value){
					return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
				}
		}]});
	},
	_toDo:function(){
		var me=this;
		var fields=['FB_CODE','FB_PRJNAME','FB_DETAIL','FB_DATE','FB_EMNAME','FB_URGENT','FB_POSITION','FB_ID','EM_NAME']; 
	    var condition='WHERE EM_NAME=?';
		return Ext.widget('gridpanel',me.gridConfig(false,{
			title:'待处理',			
			store:me.getQueryStore(fields,condition)
		}));
	},
	_alreadyLaunch:function(){
		var me=this;toDo=false;
		var fields=['FB_CODE','FB_PRJNAME','FB_DETAIL','FB_DATE','EM_NAME','FB_URGENT','FB_POSITION','FB_ID','FB_EMNAME'];
        var condition='WHERE FB_EMNAME=?';
		return Ext.widget('gridpanel',me.gridConfig(true,{
			title:'已发起',			
			store:me.getQueryStore(fields,condition)
		}));
	},
	_reject:function(){
		var me=this;toDo=false;
		var fields=['FB_CODE','FB_PRJNAME','FB_DETAIL','FB_DATE','EM_NAME','FB_URGENT','FB_POSITION','FB_ID','FB_EMNAME'];
        var condition="WHERE FB_POSITION='UNAUDITED' and FB_LASTREPLYDATE is not null and FB_EMNAME=?";
		return Ext.widget('gridpanel',me.gridConfig(true,{
			title:'被驳回',			
			store:me.getQueryStore(fields,condition)
		}));
	},	
	getQueryStore:function(fields,condition,autoLoad){
		var me=this;
		return Ext.create('Ext.data.Store',{
			fields:fields,
			proxy: {
				type: 'ajax',
				url : basePath + 'common/desktop/calls/getFeedback.action',
				method : 'GET',
				extraParams:{
					count:me.pageCount,
					condition:condition
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
		openTable(null,null,'系统问题反馈',"jsps/common/datalist.jsp?whoami=Feedback",null,null);				
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