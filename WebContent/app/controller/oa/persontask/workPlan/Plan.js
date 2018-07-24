Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workPlan.Plan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.persontask.workPlan.Plan','common.datalist.GridPanel','common.datalist.Toolbar',
     		'core.trigger.DbfindTrigger','core.form.ConDateField'
     	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpDatalistGridPanel': {
    		},
        	'button[id=ok]':{
        		click: function(btn){
        			var grid = Ext.getCmp('grid');
        			var items = grid.store.data.items;
//        			var records = grid.selModel.getSelection();
        			console.log(grid.store.data.items);
        			console.log(parent.Ext.ComponentQuery.query('fieldset'));
        			var fieldset = parent.Ext.ComponentQuery.query('fieldset');//Array
        			var summary = '';
//        			var nplan = new Array();
//        			var i = 0;
        			var num = 1;
        			fieldset[3].tfnumber = 0;
        			console.log('1:'+fieldset[3].items.items.length);
        			fieldset[3].removeAll(true);
        			fieldset[3].addBtn();
        			console.log('2:'+fieldset[3].items.items.length);
        			Ext.each(items,function(item){
        				summary += item.data.wpd_plan + '————————' + (item.data.wpd_status=='DOING' ? '未完成':'已完成') + '\n';
        				if(item.data.wpd_status == 'DOING'){
        					fieldset[3].addRecord(item.data.wpd_plan, item.data.wpd_taskid);
        				}
        			});
        			console.log('3:'+fieldset[3].items.items.length);
        			console.log(fieldset[1]);
        			fieldset[1].items.items[0].setValue(summary);
//        			parent.Ext.getCmp('nextplan').setValue(nplan.join('==###=='));
//        			alert(nplan.join('==###=='));
        		}
        	}
    	});
    }
});