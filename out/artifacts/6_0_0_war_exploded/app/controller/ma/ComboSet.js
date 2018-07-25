//问题反馈编号：2016120061
Ext.QuickTips.init();
Ext.define('erp.controller.ma.ComboSet', {
	extend: 'Ext.app.Controller',
	views:['ma.comboset.ComboSet','ma.comboset.ComboSetGridPanel'],
	   	init:function(){
		   var me =this;
		   this.control({
			   'erpComboSetGridPanel': { 
				   itemclick: this.onGridItemClick
			   }
		   });
	   	}, 
	   	onGridItemClick: function(selModel, record,e,index){//grid行选择
	   		var grid=selModel.ownerCt;
			Ext.getCmp('deletecombo').setDisabled(false);
			if(index.toString() == 'NaN'){
				index = '';
			}
			var items=grid.store.data.items;
			if(index==0&&(items[0].data['dlc_caller']==''||items[0].data['dlc_fieldname']=='')){
				return;
			}
			if(index == grid.store.data.items.length-1){//如果选择了最后一行			
				var detno=Math.ceil(items[index].data.dlc_detno);
				for(var i=0;i<10;i++){
					detno++;
					var o = new Object();
					o.dlc_caller=items[0].data['dlc_caller'];
					o.dlc_fieldname=items[0].data['dlc_fieldname'];
					o.dlc_value=null;
					o.dlc_value_en=null;
					o.dlc_value_tw=null;
					o.dlc_display=null;
					o.dlc_detno=detno;
					grid.store.insert(items.length, o);
					items[items.length-1]['index'] = items.length-1;
				}
			}
	   	}
});