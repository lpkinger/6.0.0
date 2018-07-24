Ext.define('erp.view.fa.gla.RelatedParty',{ 
	extend: 'Ext.Viewport',
	initComponent : function(){ 
		Ext.apply(this, {
			items: [{
				xtype : 'form',
				title : '关联方设置',
				frame : true,
				width: '100%',
				height: 130,
				html: '<div id="container"><div id="content">'+
				'<div style="line-height:27px;font-size:13px;margin:auto;text-align:left;">' +
				'<div>1.刷新关联方：客户资料、供应商资料、其它应收往来、其它应付往来中存在明细内部交易单位的刷新其关联方属性为是 </div>'+
				'<div>2.当前账套为主账套的，刷新主账套及其所有子账套数据；当前账套为子账套的，只刷新子账套数据</div></div></div></div>',
				bbar:['->',{
					xtype: 'erpUpdateButton'
				},{
					xtype:'erpRefreshButton',
			    	text: '刷新关联方',
			        width: 95
				},{
					xtype:'erpCloseButton'
				},'->']
			},{
				height : height-130,
			 	condition: '1=1',
				xtype : 'erpGridPanel2'
			}]
		}); 
		this.callParent(arguments); 
	}
});