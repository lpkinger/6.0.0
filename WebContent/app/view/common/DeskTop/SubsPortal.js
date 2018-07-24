Ext.define('erp.view.common.DeskTop.SubsPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet', 
	id:'subsportal',
	alias: 'widget.subsportal',	
	title: '<div class="div-left">我的订阅：</div><button class="fa-tab-btn fa-tab-btn-active">今天</button><button class="fa-tab-btn">全部</button>',
	enableTools:true,
	iconCls: 'main-subs',
	itemConfig:{
		today:'今天',
		all:'全部'					  
	},
	initComponent : function(){
		var me=this;
		Ext.apply(this,{
			items:[Ext.widget('tabpanel',{
				autoShow:true,				
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
			var condition='where 1=1';
			switch(c){				
			 case 'today':					
				condition+=" AND to_char(createdate_,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')";				
				break;
			 
			} 
			var config={				
				columnLines:false,				
				title:conf[c],				
				listeners:{
					'activate':function(tab){
						//console.log("tab:"+tab.title);
						
						tab.getStore().load();
					}
				},
				columns:[{
					text:'标题',
					dataIndex:'TITLE_',
					cls:'x-grid-header-simple',
					flex:1,
					fixed:true,
					renderer:function(val,meta,record){
						var numId=record.get('NUM_ID_');
						var mainId=record.get('INSTANCE_ID_');
						var insId=record.get('ID_');
						var title=record.get('TITLE_');
						var detail='';
						if(record.get('SUMDATA_')) {detail='<font color="#777">'+record.get('SUMDATA_')+'</font>';}
						return Ext.String.format('<a href="javascript:showWin(\''+numId+'\',\''+mainId+'\',\''+insId+'\',\''+title+'\',\''+this.id+'\');">{0} {1}</a>',
								title,
								detail
						);
					}
				},{
					text:'状态',
					draggable:false,
					cls:'x-grid-header-simple',
					width:50,
					dataIndex:'STATUS_',
					fixed:true,
					renderer: function readstatus(val,meta,record){
						if(val==-1)return '<span style="color:green">已读</span>';
						else return '<span style="color:red;">未读</span>';
					}
				},{
					text:'推送时间',
					cls:'x-grid-header-simple',
					width:150,
					dataIndex:'CREATEDATE_',
					xtype:'datecolumn',
					renderer:function(value){
						return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
					}
				}],
				store: Ext.create('Ext.data.Store', {
					fields:['ID_','NUM_ID_','INSTANCE_ID_','CREATEDATE_','TITLE_','STATUS_','EMP_ID_','SUMDATA_'],
					proxy: {
						type: 'ajax',
						url : basePath + 'common/desktop/subs/getSubs.action',
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
					autoLoad: false
				})};
				var p=Ext.create('Ext.grid.Panel',config)
				//p.id='ni';
			items.push(p);
		}
		return items;
	},
	getMore:function(){
		openTable(null,null,'更多订阅',"jsps/common/moresubs.jsp",null,null);				
	},
	tabChange: function(index) {
		this.down('tabpanel').setActiveTab(index);
	}
	
});