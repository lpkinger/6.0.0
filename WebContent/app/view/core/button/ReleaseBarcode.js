/**
 * 冻结条码按钮
 */
Ext.define('erp.view.core.button.ReleaseBarcode', {
	extend : 'Ext.Button',
	alias : 'widget.erpReleaseBarcodeButton',
	iconCls : 'x-button-icon-check',
	cls : 'x-btn-gray',
	id : 'ReleaseBarcode',
	text : $I18N.common.button.erpReleaseBarcodeButton,
	style : {
		marginLeft : '10px'
	},
	width:100,
	initComponent : function() {
		this.callParent(arguments);
	},
	handler: function(btn){
		var me = this;
		var bool = false;
		var grid = Ext.getCmp('batchDealGridPanel');
        var items = grid.selModel.getSelection();
        var idArray = [];
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		idArray.push(this.data[grid.keyField]);
        		bool = true;    		
        	}
        });
      if(bool){
      	var idS = idArray.toString();
      	me.freeze(idS);
      }else{
		  showError("没有勾选需要释放的行,请勾选");
	  }
	},
	freeze: function(idS){
		var condition = 'bar_id in ('+idS+")";
		Ext.Ajax.request({
	    	url : basePath +'scm/reserve/releaseBarcode.action',
			params: {
				caller:caller,
				condition: condition
			},
			method : 'post',
			timeout: 360000,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				console.log(res);
				if(res.success){
					Ext.MessageBox.alert('提示', '条码释放成功!',function(){
						window.location.reload();
					});
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
			}
	    });
	}
});