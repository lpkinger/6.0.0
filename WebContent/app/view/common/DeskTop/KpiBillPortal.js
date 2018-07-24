Ext.define('erp.view.common.DeskTop.KpiBillPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet', 
	alias: 'widget.kpibillportal',
	title: '<div class="div-left">考核管理：</div><button class="fa-tab-btn fa-tab-btn-active">未评分</button><button class="fa-tab-btn">已评分</button>',
	enableTools:true,
	activeRefresh:true,
	iconCls: 'main-kpi',
	itemConfig:{
		toDo:'未评分',
		alreadyDo:'已评分' 
	},
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
				items:[me._kpibill()]
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
	_kpibill:function(){
		var me=this,items=new Array(),conf=me.itemConfig;
		for(var c in conf){
		var condition='';
			switch(c){
			 case 'toDo':
				condition+="and kb_statuscode='ENTERING'";
				break;
			 case 'alreadyDo':
				 condition+="and kb_statuscode='COMMITED'";
				break;
			}
		var config={
			columnLines:false,
			title:conf[c],
			listeners:{
				'activate':function(tab){
					tab.getStore().load();
				}
			},
			columns:[{
				text:'标题',
				cls:'x-grid-header-simple',
				dataIndex:'KB_TITLE',
				flex:1,
				renderer:function(val,meta,record){
					meta.tdCls='x-grid-cell-topic1';
						var detail=record.get('KB_TITLE');			
						return Ext.String.format('<span style="color:#436EEE;padding-left:2px"><a class="x-btn-link" onclick="openTable({0},\'Kpibill\',\'评估单\',\'jsps/hr/kpi/Kpibill.jsp?caller=Kpibill\',\'kb_id\',\'kbd_kbid\',null,null);" target="_blank" style="padding-left:2px">{1}</a></span>',
								record.get('KB_ID'),	
								record.get('KB_TITLE')
						);}
			},{
				text:'考核类型',
				cls:'x-grid-header-simple',
				width:80,
				dataIndex:'KD_STARTKIND',
				filter: {
					dataIndex: 'KD_STARTKIND',
					displayField: 'display',
					queryMode: 'local',
					store: {data: [{display: "周考核", value: "week"},
								   {display: "月度考核", value: "month"},
		                           {display: "季度考核", value: "season"},
		                           {display: "手动考核", value: "manual"}],
		                           fields: ["display", "value"]
							},
					valueField: "value",
					xtype: "combo"
					},
				xtype: 'combocolumn'
			},{
				text:'受评人',
				cls:'x-grid-header-simple',
				width:80,
				dataIndex:'KB_BEMAN'
			},{
				text:'截止日期',
				cls:'x-grid-header-simple',
				xtype:'datecolumn',
				width:100,
				dataIndex:'KB_ENDDATE',
				renderer:function(value){
					return Ext.Date.format(new Date(value),'Y-m-d');
				}
			}],
			store:Ext.create('Ext.data.Store',{
					fields:['KB_ID','KB_TITLE','KD_STARTKIND','KB_BEMAN','KB_ENDDATE'],
					proxy: {
						type: 'ajax',
						url : basePath + 'common/desktop/kpi/getKpibill.action',
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
				})};
				items.push(Ext.create('Ext.grid.Panel',config));
		};
		return items;
	},
	getMore:function(){
		openTable(null,null,'更多评估单',"jsps/common/datalist.jsp?whoami=Kpibill&urlcondition=kb_manid=session:em_uu",null,null);				
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