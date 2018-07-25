Ext.define('erp.view.opensys.billPlanTrace.OrderProcessChart',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.OrderProcessChart',
	id:'OrderProcessChart',
	autoScroll:true,
	initComponent : function(){
		var me = this;
		var view = me.view = new Ext.DataView({
			store : Ext.create('Ext.data.Store', {
				fields: ['text','detno','start','end','remark','status'],
				data:[]
			}),
			tpl : new Ext.XTemplate(
				'<div class="x-module-parent ">',
					'<tpl for=".">',
						'<div class="x-module-item ',
						'<tpl if="status==\'finish\'">finish path"</tpl>', //状态
						'<tpl if="status==\'running\'">running path"</tpl>',
						'<tpl if="status==\'gray\'">gray "</tpl>',
						' style="margin-left:{detno*120}px; margin-top: 2px">',
						'<span class="font-style" data-qtip="',
						'<tpl if="start != \'\'">开始时间:{start}</tpl>',
						'<tpl if="end != \'\'"></br>结束时间:{end}</tpl>',
						'<tpl if="remark != \'\'"></br>备注:{remark}</tpl>',
						'">{text}</span>',
						'</div>',
			    	'</tpl>',
				'</div>'
			),
			trackOver: true,
			overItemCls : 'x-module-over',
			selectedClass : 'selected',
			singleSelect : true,
			itemSelector : '.x-module-item'
		});
		Ext.apply(me, {
			items: [view]
		});
		this.callParent(arguments);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
		afterrender: function(me){	
			var code = Ext.getCmp('sa_pocode')?Ext.getCmp('sa_pocode').value:'';
			Ext.Ajax.request({
				url: basePath + 'common/VisitERP/orderProcess.action',
				params: {
					purchaseCode: code
				},
				async:false,
				callback: function(options, success, response){
					var res = Ext.decode(response.responseText);
					me.view.store.removeAll();
					me.view.store.add(res);
				}
			});
		}
	}
});