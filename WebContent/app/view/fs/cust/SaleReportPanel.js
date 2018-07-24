Ext.define('erp.view.fs.cust.SaleReportPanel', {
		extend: 'Ext.panel.Panel',
		alias: 'widget.navpanel',
		layout:'fit',
		bodyPadding: '0 0 0 10',
		autoScroll : true,
		data : null,
		items: [{
				frame: true,		
				xtype: 'container',
				layout: 'anchor',
				minWidth:1118,
				autoHeight:true,
				id:'content',
				defaults: {
					bodyStyle: 'background:#ffffff;border-width:0px',
					xtype:'dataview',
					border:0,
					style:'width:100%;height:auto;'
				},
				items: [
					{				
						id:'order'
					},{
						id:'deposit'
					},{
						id:'purchase'
					},{
						id:'make'
					},{
						id:'accept'
					},{
						id:'saleout'
					},{
						id:'payforAR'
					}]
		}],
		initComponent: function(){
			this.getSaleReportDetail();
			this.callParent();
		},
		getSaleReportDetail : function(){
			var me = this;
			Ext.Ajax.request({
				url:basePath + 'fs/cust/getSaleReportDetail.action',
				params:{
					custcode : custcode,
					ordercode : ordercode
				},
				method:'post',
				async: false,
				callback:function(options,success,resp){
					var res = new Ext.decode(resp.responseText);
					if(res.success){
						me.data = res;
					}else if(res.exceptionInfo){
						showError(res.exceptionInfo);	
					}
				}
			});
		}
	})