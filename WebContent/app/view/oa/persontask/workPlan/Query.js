Ext.define('erp.view.oa.persontask.workPlan.Query',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'north',          
		    	anchor: '100% 30%',
		    	tbar: [{
		    		name: 'query',
		    		text: $I18N.common.button.erpQueryButton,
		    		iconCls: 'x-button-icon-query',
		        	cls: 'x-btn-gray',
		        	handler: function(){
		    			var grid = Ext.getCmp('grid');
		    			var form = Ext.getCmp('form');
		    			var condition = '';
		    			Ext.each(form.items.items, function(f){
		    				if(f.logic != null && f.logic != '' && f.value != null && f.value != ''){
		    					if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
		    						if(condition == ''){
		    							condition += f.logic + " " + f.value;
		    						} else {
		    							condition += ' AND ' + f.logic + " " + f.value;
		    						}
		    					} else {
		    						if(condition == ''){
		    							condition += f.logic + "='" + f.value + "'";
		    						} else {
		    							condition += ' AND ' + f.logic + "='" + f.value + "'";
		    						}
		    					}
		    				}
		    			});
		    			if(condition != ''){
		    				grid.getCount('WorkPlan!Query', condition);
		    			} else {
		    				showError('请填写筛选条件');return;
		    			}		    			
		        	}
		    	}, '-', {
		    		text: $I18N.common.button.erpCloseButton,
		    		iconCls: 'x-button-icon-close',
		        	cls: 'x-btn-gray',
		        	handler: function(){
		        		var main = parent.Ext.getCmp("content-panel"); 
		        		main.getActiveTab().close();
		        	}
		    	}]
		    },{
		    	  region: 'south',         
		    	  xtype:'erpDatalistGridPanel',
		    	  anchor: '100% 70%'	    	  
		    }]
		}); 
		me.callParent(arguments); 
	} 
});