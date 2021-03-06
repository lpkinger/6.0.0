Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOMStructQuery', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.batchPrint.Viewport','common.batchPrint.Form','pm.bom.BomQueryGrid','core.form.ConMonthDateField',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.form.MonthDateField',
     		'core.form.YnField', 'core.form.FtDateField','core.grid.YnColumn', 'core.grid.TfColumn',
			'core.form.ConMonthDateField','core.button.BOMAttachDownload'],
    init:function(){
    	//var me = this;
    	this.control({  
       		'erpBatchPrintGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('printform');
        			if(form && form.items.items.length > 0){
        				//根据form字段的多少来调节form所占高度
        				var height = window.innerHeight;
            			var cw = 0;
            			Ext.each(form.items.items, function(){
            				cw += this.columnWidth;
            			});
            			cw = Math.ceil(cw);
            			if(cw == 0){
            				cw = 5;
            			} else if(cw > 2 && cw <= 5){
            				cw -= 1;
            			} else if(cw > 5 && cw < 8){
            				cw = 4;
            			}
            			cw = Math.min(cw, 5);
            			form.setHeight(height*cw/10 + 10);
            			grid.setHeight(height*(10 - cw)/10 - 10);
        			}
    			}
    		},
    		'button[name=batchPrint]': {
    			afterrender: function(btn){ 
    				// btn.hide();
    			}
    		}
    	});
    },
    countGrid: function(){
    	//重新计算合计栏值
    	var grid = Ext.getCmp('batchPrintGridPanel');
    	Ext.each(grid.columns, function(column){
			if(column.summary){
				var sum = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						sum += Number(item.value);
					}
				});
				Ext.getCmp(column.dataIndex + '_sum').setText(column.text + '(sum):' + sum);
			} else if(column.average) {
				var average = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						average += Number(item.value);
					}
				});
				average = average/grid.store.data.items.length;
				Ext.getCmp(column.dataIndex + '_average').setText(column.text + '(average):' + average);
			} else if(column.count) {
				var count = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						count++;
					}
				});
				Ext.getCmp(column.dataIndex + '_count').setText(column.text + '(count):' + count);
			}
		});
    }
});