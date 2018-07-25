Ext.define('erp.view.b2c.purchase.B2CPurchase.Grid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpB2CPurchaseGrid',
	requires: ['erp.view.core.grid.ButtonColumn'],
	layout : 'fit',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,  
    prodcode:'',
	bbar: ['->',{
	    xtype: 'tbtext',
	    text: '已选购数量:0',
	    tpl: '已选购数量:{haveget}',
	    name:'haveget'
    	/*var remind=records.length>0?Ext.String.format('<p style="color:#436EEE;padding-left:20px;font-size:14px;">未匹配物料:{0}条;商城中不存在的品牌、器件，您可以提交品牌入库申请、器件入库申请 </p>',
    			    		records.length
					):'';
				}
			    Ext.getCmp('dealform').down('#reminder').setText(remind);*/
	},'-',{
		xtype: 'tbtext',
	    text: '金额:0',
	    tpl: '金额:{sumprice}',
	    name:'sumprice'
	}],
	columnLines: true,
	selType: 'cellmodel',
	/*store: Ext.create('Ext.data.Store',{
		fields: ['gb_b2bbatchcode','gb_price','gb_deliverytime','gb_madedate','gb_onsaleqty','gb_minbuyqty','gb_minpackqty','buyQty','gb_minprice'],			  
        data: [],
        autoLoad:true
     }),*/
	columns: [{
		xtype : 'rownumberer',
		width : 35,
		sortable : false,
		text: '#',
		align :'center'
	},{
		text: '批次',
		dataIndex: 'gb_b2bbatchcode',
		flex: 1.8
	},{
		text: '单价',
		dataIndex: 'gb_price',
		flex: 2,	
		renderer:function(val, meta, record){
			 meta.style='white-space: normal;height: auto!important;';
			 var da = new Ext.decode(val);
             var html = '';
			 Ext.Object.each(da,function(key, value){
			 	html +='<div>'+value.start+'~'+value.end+'个：<span style="color:red">';
			 	if(value.rMBNTPrice){
			 		html +=' ￥'+value.rMBNTPrice;
			 	}
			 	if(value.uSDNTPrice){
			 		html +=' $'+value.uSDNTPrice;
			 	}
			 	html +=' </span></div>';
			 });
			 return html;
		},
		filter:{}
	},{
		text: '大陆交期',
		dataIndex: 'gb_deliverytime',
		flex: 0.5,
		renderer:function(val, meta, record){
			 return val+'天';
		}
	},{
		text: '香港交期',
		dataIndex: 'gb_hkdeliverytime',
		flex: 0.5,
		renderer:function(val, meta, record){
			 return val+'天';
		}
	},{
	    text: '批次日期',
		dataIndex: 'gb_madedate',
		flex: 1.0,
		xtype: 'datecolumn',
		renderer:function(val, meta, record){
			return Ext.Date.format(new Date(val),'Y-m-d');
		}
	},{
	    text: '库存',
		dataIndex: 'gb_onsaleqty',
		flex: 0.8
	},{
		text: '最小起订量',
		dataIndex: 'gb_minbuyqty',
		flex: 0.8
	},{
		text: '最小包装数',
		dataIndex: 'gb_minpackqty',
		flex: 0.8
   },{
		text: '购买数量',
		dataIndex: 'buyQty',
		xtype: 'numbercolumn',
		flex: 1,
		renderer:function(val, meta, record){
			var minbuy = record.data['gb_minbuyqty'];
			var minpack = record.data['gb_minpackqty'];
			if(val!="" && val!=null && val <= 0){
				showError('购买数量必须大于0！');
                val = "";
			}
			if((val!="" && val!=null)&&((val>minpack && (val % minpack)) || val < minbuy)){
				showError('购买数量必须大于最小起订量并且是最小包装数的整数倍！');
				val = "";
			}
			if(val > record.data['gb_onsaleqty']){
				showError('购买数量不允许超过库存！');
				val = "";
			}
			if(record.data['buyQty'] != val) {
				record.set('buyQty', val);
			}	
			//统计购买数量
			var grid = this;
			var currency = Ext.getCmp("currency").value;
			var sum1 = 0;
			var gdata = [];
			var sumprice = 0;
			grid.store.each(function(record){  
				var buyqty = Number(record.data.buyQty);
				 var da = new Ext.decode(record.data['gb_price']);
				 Ext.Object.each(da,function(key, value){
				     if((value.start<buyqty || value.start==buyqty) && (value.end >buyqty || value.end ==buyqty)){
				     	sumprice += record.data.buyQty*value.rMBNTPrice;
				     }
				 });
			   sum1 += Number(record.data.buyQty);  
			}); 
			var puqty = Ext.getCmp('form'+grid.prodcode).down('field[name=puqty]').value;
			if(sum1 > puqty){
				showError('购买数量不允许超过请购数量！');
				val = "";
			}
			grid.down("tbtext[name='haveget']").setText('已选购数量:' + sum1);
			grid.down("tbtext[name='sumprice']").setText('金额:' + sumprice+" "+currency);
			grid.sumprice = sumprice;
	        //更新待选购数量
			Ext.getCmp('form'+grid.prodcode).down('field[name=needbuyqty]').setValue(puqty-sumprice);
			//重新统计汇总金额
			var grids = Ext.getCmp('buyContainer').query('grid');
			var totalprice = 0;
			Ext.Array.each(grids, function(value) {
		        totalprice+=value.sumprice;
		    });
		    Ext.getCmp('totalprice').setText("汇总金额:"+totalprice+" "+currency);
			return val;
		},
		editor:{
			 xtype: 'numberfield',
			 msgTarget: 'under'
		}
	   },{
			xtype : 'numbercolumn',
			dataIndex: 'gb_minprice',
			sortable : true,
			align :'center',
			hidden:true
	},{
			dataIndex: 'gd_currency',
			hidden:true
	}],				 
	initComponent : function(){ 
		this.callParent(arguments);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	}

});