Ext.define('erp.view.common.DeskTop.CallPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet',
	title: '<div class="div-left">客户生日提醒：</div><button class="fa-tab-btn fa-tab-btn-active">今天</button><button class="fa-tab-btn">近七天</button><button class="fa-tab-btn">30天内</button><button class="fa-tab-btn">全部</button>',
	iconCls: 'main-notice',
	enableTools:true,
	alias: 'widget.callportal',
	//activeRefresh:true,
	//autoRefresh:true,
	itemConfig:{
		today:'今天',
		nearWeek:'近七天',
        nearMonth:'30天内',
		all:'全部'	  
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
				items:[me._custbirth()]
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
	_custbirth:function(){
		var me=this,items=new Array(),conf=me.itemConfig;
		for(var c in conf){
			var condition='';
			switch(c){
			 case 'today':
				condition+="WHERE DAYS=0";
				break;
			 case 'nearWeek':
				 condition+="WHERE DAYS>=0 AND DAYS<7";
				break;
			 case 'nearMonth':
				condition+="WHERE DAYS>=0 AND DAYS<30"; 
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
					text:'客户',
					draggable:false,
					fixed:true,
					cls:'x-grid-header-simple',
					flex:1,
					dataIndex:'CU_ID',
					renderer: function(val, meta, record){	
						return Ext.String.format('<a class="x-btn-link" onclick="openTable({1},\'Customer!Base\',\'客户\',\'jsps/drp/distribution/customerType.jsp\',\'cu_id\',null,null,null);">{2} {0}</a>',
								record.get('CU_NAME'),
								record.get('CU_ID'),
								record.get('CU_CODE'));
					}
				},{
					text:'联系人',
					draggable:false,
					cls:'x-grid-header-simple',
					width:100,
					fixed:true,
					dataIndex:'CT_NAME',
					renderer: function(val, meta, record){	
						return Ext.String.format('<a class="x-btn-link" onclick="openTable({1},\'Contact\',\'客户联系人\',\'jsps/crm/customermgr/development/contact.jsp\',\'ct_id\',null,null,null);">{0}</a>',
								record.get('CT_NAME'),
								record.get('CT_ID'));
					}
				},{
					text:'生日',
					draggable:false,
					cls:'x-grid-header-simple',
					width:100,
					fixed:true,
					xtype:'datecolumn',
				    renderer:function(value){
					      return Ext.Date.format(new Date(value),'Y-m-d');
					    },
					dataIndex:'BIRTHDAY'				
				},{
					text:'倒计时/天',
					draggable:false,
					cls:'x-grid-header-simple',
					width:80,
					fixed:true,
					dataIndex:'DAYS'		
				}],
				store:Ext.create('Ext.data.Store',{
					fields:['CU_ID','CU_CODE','CU_NAME','CT_ID','CT_NAME','BIRTHDAY','DAYS'],
					proxy: {
						type: 'ajax',
						url : basePath + 'common/desktop/calls/getCustBirth.action',
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
		}
		return items;
	},
	getMore:function(){
		openTable(null,null,'我的客户联系人',"jsps/common/datalist.jsp?whoami=Contact!Distr&urlcondition=cd_sellercode=session:em_code&_noc=1",null,null);				
	},/*
	_dorefresh:function(panel){
		var activeTab=panel.down('tabpanel').getActiveTab();
		if(activeTab) activeTab.fireEvent('activate',activeTab);
	}*/
	tabChange: function(index) {
		this.down('tabpanel').setActiveTab(index);
	}
});