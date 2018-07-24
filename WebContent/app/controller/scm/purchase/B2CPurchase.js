Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.B2CPurchase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:['scm.purchase.B2CPurchase.Form','scm.purchase.B2CPurchase.Grid'	,
    		'scm.purchase.B2CPurchase.B2CPurchase'
    	 ],
    init:function(){
    	var me = this;
		this.control({			
			'#buyContainer':{
				beforerender:function(){//加载数据
					var prcodes = '';
					Ext.each(groups,function(name,index){
						prcodes +=name.keys.ad_prodcode+",";
					});
					me.getReserve(prcodes.substring(0, prcodes.length - 1));
				}
			},
			'#currency':{
				dirtychange:function(t,isDirty){
					var grids = Ext.getCmp('buyContainer').query('grid');
					var filter = '';
					if(t.value == 'RMB'){
						filter = new Ext.util.Filter({
        				    filterFn: function(item) {
        				    	var bool = true;
        				    	if(item.data['gb_currency'] == 'USD'){
        				    		item.set('buyQty', 0);
        				    		bool = false;
        				    	}
        				    	return bool;
        				 }});
					}else if(t.value == 'USD'){
						filter = new Ext.util.Filter({
        				    filterFn: function(item) {
        				    	var bool = true;
        				    	if(item.data['gb_currency'] == 'RMB'){
        				    		item.set('buyQty', 0);
        				    		bool = false;
        				    	}
        				        return bool;
        				 }});
					}
					Ext.Array.each(grids, function(grid) {
				    	grid.store.clearFilter(true);
						grid.store.filter(
						   filter
        				);
				    });
				}
			},
			'#confirmBuyBtn':{//确认购买
				click:function(btn){//确认平台采购
					//限制美元总价不允许小于100USD,人民币不允许小于500RMB
					var currency = Ext.getCmp('currency').value;
					var totalprice = Ext.getCmp('totalprice').value ;
					/*if(currency == 'RMB' && totalprice<500){
						showError('交易总金额必须到500RMB才允许交易！');
					}else if(currencyy == 'USD' && totalprice<100){
						showError('交易总金额必须到100RMB才允许交易！');
					}*/
					//依次判断每个物料选择的数量不允许大于请购数即可 
					var forms = Ext.getCmp('buyContainer').query('form');
					Ext.Array.each(forms, function(form,index) {
						if(index > 1){
					        if(form.getForm().findField('needbuyqty').getValue()<0){
					        	showError("物料"+form.getForm().findField('pr_code').getValue()+"已选购数量大于请购数");
					        }
						}
				    });
				    var tdata = new Object();
				    var grids = Ext.getCmp('buyContainer').query('grid');
				    var bo = false;
				    Ext.Array.each(grids, function(grid) {
				    	var griddata = [];
				    	grid.store.each(function(record){     
						   if(Number(record.data.buyQty)>0){
						   	var data = {};
						   	data.gb_b2bbatchcode = record.data['gb_b2bbatchcode'];
						   	data.buyQty = record.data['buyQty'];
						   	griddata.push(data);
						   }
						}); 
						if(griddata.length>0){
							bo = true;
							tdata[grid.prodcode]=griddata; 
						}
				    });
				    if(bo){
				    	//记录已选批次数据，传送至后台进行
						me.confirmB2CPurchase(unescape(escape(Ext.JSON.encode(tdata))),currency);
					}else{
						showError("未选择需要采购的数据");
					}
				}
			}			
		});
	},
	getReserve:function(code){		
		var me = this;
		Ext.Ajax.request({//设置列表caller
			url : basePath + "scm/turnPurchase/getReserveByUUid.action",
			params: {
				pr_code : code					
			},
			method : 'post',
			async: false,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				if(res.success){
					if(res.data){
						var data = res.data;
						for(var a in data){
							var needbuy = 0;
							Ext.each(groups,function(name,index){
								if(name.keys.ad_prodcode == a){
									needbuy = name.totalqty;
								}
					        });
						    me.addFormAndGrid(a,data[a],needbuy);
						}
					}else{
						showError("暂无可购买库存");
					}
				}
			}
		});		
	},
	addFormAndGrid:function(code,detail,needbuy){  //获取采购数据
		Ext.each(detail,function(value, key){
			if(value.gb_price) {
				value.gb_minprice = new Ext.decode(value.gb_price)[0].price;
				value.gb_minpackqty = value.gb_minpackqty || 1;
				value.gb_minbuyqty = value.gb_minbuyqty || 1;
			}
		});
		var panel = Ext.getCmp('buyContainer') ,items=[];
		items.push({
		      id:'form'+code,
		      xtype:'erpB2CPurchaseForm'
		});
		items.push({
		     id:'grid'+code,
		     xtype:'erpB2CPurchaseGrid',
		     store: Ext.create('Ext.data.Store',{
				 fields: ['gb_b2bbatchcode','gb_price','gb_deliverytime','gb_madedate','gb_onsaleqty','gb_minbuyqty','gb_minpackqty','buyQty','gb_minprice','gb_currency','gb_hkdeliverytime'],			  
		         data: detail,
		         autoLoad:true
		     }),
		    prodcode:code,
		    sumprice:0,
	     	plugins : [Ext.create('erp.view.core.plugin.CopyPasteMenu'),
		    Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit : 1
				})]
		 });
		panel.add(items);
		//默认币别人民币
		Ext.getCmp('grid'+code).store.filter(
		       filter = new Ext.util.Filter({
			       filterFn: function(item) {
				    	var bool = true;
				    	if(item.data['gb_currency'] == 'USD'){
				    		bool = false;
				    	}
				        return bool;
			      }
			 })
		);
		Ext.getCmp("form"+code).down("field[name='pr_code']").setValue(code);
		Ext.getCmp("form"+code).down("field[name='totalqty']").setValue(detail[0].go_onsaleqty);
		//请购数量
		Ext.getCmp("form"+code).down("field[name='puqty']").setValue(needbuy);
		//待选购数量
		Ext.getCmp("form"+code).down("field[name='needbuyqty']").setValue(needbuy);
		
	},
	confirmB2CPurchase:function(param,currency){
		    //需从平台下单数据
		    var data = window.parent.Ext.getCmp('B2CPurchaseButton').getTurnData();
		    //param 需转采购数据		  
			Ext.Ajax.request({
				url : basePath + "scm/turnPurchase/comfirmB2CPurchase.action",
				params: {
					param:param,
					data:data.data,
					caller:	data.caller	,
					currency:currency
				},
				method : 'post',
				async: false,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exceptionInfo){
						showError(res.exceptionInfo);return;
					}
					if(res.success){
						if(res.log){
	    					showMessage("提示", res.log, 15000);
	    				}
		   				Ext.Msg.alert("提示", "处理成功!", function(){
		   					window.parent.Ext.getCmp("batchDealGridPanel").multiselected = new Array();
		   					window.parent.Ext.getCmp('dealform').onQuery();
		   				});
						parent.Ext.getCmp("dlwin").close();
					}
				}
			});		
	   }
});