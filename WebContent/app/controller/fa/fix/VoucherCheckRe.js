Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.VoucherCheckRe', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.grid.Panel4','core.toolbar.Toolbar3','core.button.Save'
      	
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel4': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.beforeUpdate();

    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    /*	this.gridLastSelected = record;
    	var grid = Ext.getCmp('grid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);*/
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(){
	
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var me = this;
		Array.prototype.contains = function(obj) {
		    var i = this.length;
		    while (i--) {
		        if (this[i] === obj) {
		            return true;
		        }
		    }
		    return false;
		};
	
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var rowNo = [];
		Ext.each(items,function(item,index){
		
		Ext.each(grid.columns, function(c){
			if(item.data[c.dataIndex]!=item.raw[c.dataIndex])
				{
				if(!rowNo.contains(index+1)){
					
					rowNo.push(index+1);
				}
				}
			
			});
	 
		});
	
		if(rowNo.length==0){
			Ext.Msg.alert("你未对数据做任何修改!");
			return;
		}
		else{
			// confirm box modify
			// zhuth 2018-2-1
			Ext.Msg.confirm("提示", "第"+rowNo.toString()+"行已经修改,确定更新?", function(btn) {
				if(btn == 'yes') {
					var index = 0;
					var jsonGridData = new Array();
					var s = grid.getStore().data.items;
					for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
						var data = s[i].data;
							Ext.each(grid.columns, function(c){
							if(c.xtype == 'datecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									data[c.dataIndex] = Ext.Date.toString(data[c.dataIndex]);//在这里把GMT日期转化成Y-m-d格式日期
								} else {
									data[c.dataIndex] = '1970-01-01';//如果用户没输入日期，或输入有误，就给个默认日期，
									//或干脆return；并且提示一下用户
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
									data[c.dataIndex] = '0';
								}
							}
						});
						jsonGridData[index++] = Ext.JSON.encode(data);
					}
					
					me.update(jsonGridData.toString());
				}
			});
		}
	
		
	
	
		
	},
	update:function(param){
		Ext.Ajax.request({
		    url: basePath+'fa/fix/updateVoucherCheckRe.action',
		    params:{
		    	param:param
		    },
		    success: function(response){
		        var text = response.responseText;
		        result = Ext.decode(text);
		        if(result.success){
		        	Ext.Msg.alert("保存成功！"); // 尚未国际化，以后订正。
		        }
		    }
		});
		
	}
		
});