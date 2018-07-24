Ext.QuickTips.init();
Ext.define('erp.controller.b2b.sale.QuotationDown', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','b2b.sale.QuotationDown','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField','core.button.TurnSale',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
  				'core.button.Audit','core.button.Close','core.button.Delete','core.button.DeleteDetail','core.button.ResSubmit','core.button.TurnCustomer',
  				'core.button.ResAudit',	'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn'
      	],
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			afterrender: function(g) {
                    g.plugins[0].on('beforeedit', function(args) {
                    	var clean = true;
                    	Ext.Array.each(g.store.data.items,function(item){
                    		if(item.data.qd_id!=''||item.data.qd_id!='0'){
                    			clean = false
                    		}
                    	});
                    	if(clean&&g.custLap&&!g.overClean){
                    		g.store.removeAll();
	    					g.store.add({});
	    					g.overClean = true;
	    					return false;
                    	}
                        if (g.readOnly) {
                            return false;
                        }
                        if(me.isCustLap && args.field == 'qd_lapqty')
                        	return false;
                        if(args.rowIdx == 0 && args.field == 'qd_lapqty' && args.value == 0)
                        	return false;
                    });
                    g.plugins[0].on('afteredit', function(a,b,c) {
                    	if(b.field=='qd_lapqty'){
                    		g = Ext.getCmp('grid');
                    		var value = b.value;
                    		var index = b.rowIdx;
                    		if(index>1){
                    			var preValue = g.store.data.getAt(index-1).get('qd_lapqty')
                    			if(preValue>=value||preValue=='0'){
                    				showError('分段数量必须按照从小到大顺序填写，请重新填写');
                    				g.store.data.getAt(index).set('qd_lapqty',null);
                    				g.store.data.getAt(index).set('qd_price',null);
                    			}
                    		}
                    	}
                    	
                    });
                }
    		},    		
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				g = Ext.getCmp('grid');
    				var bad = false;
    				Ext.Array.each(g.store.data.items,function(item,index){
    					if(index!=0){
    						if(item.data.qd_lapqty!='0'||item.data.qd_price!='0'){
    							if(item.data.qd_lapqty=='0'||item.data.qd_price=='0'){
    								bad = true
    							}
	                		}
    					}else{
    						if(item.data.qd_price=='0'){
    							bad = true
    						}
    					}
                	});
                	if(bad){showError('明细数据有误，请修改正确后再保存');return;}
    				this.FormUtil.onUpdate(this);    				
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');    				
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}  				
    			},
    			click: function(btn){
    				var end = Ext.getCmp('qu_enddate').value;
    				var endDate=new Date(end.getFullYear(), end.getMonth()+1, end.getDate(), 23, 59, 59);//截止日期的最后期限
    				if(endDate < new Date()){
    					bool=false;
    					showError('有效期小于当前日期，不能更新!');return;
    				}else{
    					g = Ext.getCmp('grid');
	    				var bad = false;
	    				Ext.Array.each(g.store.data.items,function(item,index){
	    					if(index!=0){
	    						if(item.data.qd_lapqty!='0'||item.data.qd_price!='0'){
	    							if(item.data.qd_lapqty=='0'||item.data.qd_price=='0'){
	    								bad = true
	    							}
		                		}
	    					}else{
	    						if(item.data.qd_price=='0'){
	    							bad = true
	    						}
	    					}
	                	});
	                	if(bad){showError('明细数据有误，请修改正确后再保存');return;}
     				    me.FormUtil.onSubmit(Ext.getCmp('qu_id').value);
    				} 
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('qu_id').value);
    			}
    		},
    		'#qu_custlap': {
    			afterrender: function(f) {
    				if(f.getValue() == 1 || f.getValue() == 0) {
    					me.isCustLap = true;
    					Ext.defer(function(){
    						var grid = Ext.getCmp('grid');
    						grid && (grid.custLap = true);
    					}, 200);
    				}
    			},
    			change:function(f){
    				if(f.rendered){
    					if(f.getValue() == 1 || f.getValue() == 0) {//分段报价
	    					me.isCustLap = true;
	    					var grid = Ext.getCmp('grid');
	    					Ext.defer(function(){
	    						grid && (grid.custLap = true);
	    					}, 200);
	    					me.deleteOldData();
	    					grid.store.removeAll();
	    					grid.store.add({});
	    				}else{//不分段报价
	    					me.isCustLap = false;
	    					var grid = Ext.getCmp('grid');
	    					Ext.defer(function(){
	    						grid && (grid.custLap = false);
	    					}, 200);
	    					me.deleteOldData();
	    					grid.store.removeAll();
	    					grid.store.add({});
	    				}
    				}
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择    	 
    	 this.GridUtil.onGridItemClick(selModel, record);
    	 if(this.isCustLap) {
    		 var grid = selModel.ownerCt, btn = grid.down('erpAddDetailButton');
    		 btn && btn.setDisabled(true);
    		 btn = grid.down('erpDeleteDetailButton');
    		 btn && btn.setDisabled(true);
    	 }
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	deleteOldData:function(){
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + '/b2b/sale/deleteQuotationDownDetail.action',
        	params: {
        		id:Ext.getCmp('qu_id').value
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}       
        	}
        });
	}
});