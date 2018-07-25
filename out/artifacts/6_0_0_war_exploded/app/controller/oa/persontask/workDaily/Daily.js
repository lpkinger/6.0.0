Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workDaily.Daily', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.persontask.workDaily.Daily','common.datalist.GridPanel','common.datalist.Toolbar',
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
        			var records = grid.selModel.getSelection();
        			var fieldset = parent.Ext.ComponentQuery.query('detailtextfield')[0];
        			if(records.length > 0){
        				var values = new Array();
        				Ext.each(records, function(record, index){
        					values[index] = record.data.wr_taskname + 
        					'  今日提交完成率 ' + record.data.wr_percentdone + '% ' + 
        					' 整个任务完成率 ' + (record.data.wr_percentdone+record.data.wr_taskpercentdone) + '%';
        					if(parent.Ext.getCmp('text'+(index+1))){
        						parent.Ext.getCmp('text'+(index+1)).setValue(values[index]);        						
        					} else {
        						fieldset.addItem(Ext.create('Ext.form.field.Text', {
        							xtype: 'textfield',
        							name: 'text' + ++fieldset.tfnumber,
        							id: 'text' + fieldset.tfnumber,
        							columnWidth: 0.95,
        							value: '',
        							fieldLabel: '第&nbsp;' + fieldset.tfnumber +'&nbsp;条',
        							fieldStyle: 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
        							listeners:{
        								change: function(){
        							    	var s = '';
//        							    	alert(12);;
//        									for(var i=1; i<=fieldset.tfnumber; i++){
//        										if(Ext.getCmp('text'+i).value != null && Ext.getCmp('text'+i).value.toString().trim() != ''){
//        											s += Ext.getCmp('text'+i).value + '==###==';
//        										}
//        									}
        									fieldset.value = fieldset.getValue();
        								}
        							}
        						}));
        						fieldset.addItem(Ext.create('Ext.button.Button', {
        		 					text: '清&nbsp;空',
        		 					name: 'btn' + fieldset.tfnumber,
        							id: 'btn' + fieldset.tfnumber,
        							columnWidth: 0.05,
        							index: fieldset.tfnumber,
        							handler: function(btn){
        						        fieldset.clean(btn.index);
        		 			    	}
        		 				}));
        						parent.Ext.getCmp('text'+fieldset.tfnumber).setValue(values[index]);
        					}
        				});
        				
        			}
        		}
        	}
    	});
    }
});