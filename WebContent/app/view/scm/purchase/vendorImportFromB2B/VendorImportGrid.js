/**
 * 可引进供应商
 * */
var vendorProdStore = Ext.create('Ext.data.Store', {
	fields : [
	{
		name : 'en_name'
	},{//简称
		name : 'en_shortname'
	},{//地址
		name : 'en_address'
	},{//电话
		name : 'en_tel'
	},{//公司邮箱
		name : 'en_email'
	},{//供应商uu
		name : 'en_uu'
	},{//法定代表人
		name : 'enCorporation'
	},{//营业执照号
		name : 'en_businesscode'
	},{//业务范围
		name : 'en_tags'
	},{//行业
		name : 'en_profession'
	},{//联系人
		name : 'en_contactman'
	},{//联系人手机号
		name : 'en_contacttel'
	},{//物料匹配数
		name : 'hitNums'
	},{//币别
		name : 'en_currency'
	},{//是否是供应商
		name:'isVendor'
	},{//物料信息
		name : 'productInfo'
	}
	],
	autoLoad : true,
	pageSize : pageSize,
	proxy : {
		type : 'ajax',
		method:'post',
		url : basePath + 'scm/purchase/getVendorImportFromB2B.action',
		headers : { "Content-Type" : 'application/json' },
		extraParams: {
            caller:'VendorImport'
        },
        timeout:180000,
		reader : {
			encode : true,
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		}
	},
	listeners : {
		beforeload : function() {
			var form = Ext.getCmp('erpVendorImportFormPanel');
			var grid = Ext.getCmp('erpVendorImportGridPanel');
			if(this.defaultCondition&&this.defaultCondition!=""){
				Ext.apply(grid.getStore().proxy.extraParams, {
			    		defaultCondition: defaultCondition
					});
			}
			if(form){
				var condition = form.getSearchCondition();
				if(grid&&condition){
			    	Ext.apply(grid.getStore().proxy.extraParams, {
			    		condition: condition
					});
				}
			}
		}
	}
});
Ext.QuickTips.init();
Ext.define('erp.view.scm.purchase.vendorImportFromB2B.VendorImportGrid',{
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpVendorImportGridPanel',
	region: 'south',
	layout : 'fit',
	id: 'erpVendorImportGridPanel',
	requires: [ 'erp.view.core.plugin.CopyPasteMenu'],
	store: vendorProdStore,
	plugins: [ Ext.create('erp.view.core.grid.HeaderFilter'),
	Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	autoScroll : true,
    columns: [
    Ext.create('Ext.grid.RowNumberer' , {
			text : '序号',
			width : 40 ,
			height :25 ,
			align : 'center',
			cls : 'x-grid-header-1'
		}),{
        	text: '企业名', 
        	width:200,
        	dataIndex:'en_name',
        	renderer:function(value,metaData ,record){ 
	        	if(null != value){
	        		metaData.tdAttr = 'data-qtip="'+ this.gettips(this,record) +'"';  
	        		return value;  
	           	}else return null;   
	        } 
        }/*,{
        	text: '简称', 
        	dataIndex:'en_shortname',
        	renderer:function(value,metaData ,record){ 
	        	if(null != value){
	        		metaData.tdAttr = 'data-qtip="'+ this.gettips(this,record) +'"';  
	        		return value;  
	           	}else return null;   
	        }
	    }*/,{
	    	text: '电话',
	    	dataIndex:'en_tel',
	    	width:100,
	    	renderer:function(value,metaData ,record){ 
	        	if(null != value){
	        		metaData.tdAttr = 'data-qtip="'+ this.gettips(this,record) +'"';  
	        		return value;  
	           	}else return null;   
	        }
	    },{
        	text: '地址', 
        	width:200,
        	dataIndex:'en_address',
        	renderer:function(value,metaData ,record){ 
	        	if(null != value){
	        		metaData.tdAttr = 'data-qtip="'+ this.gettips(this,record) +'"';  
	        		return value;  
	           	}else return null;   
	        }
	    }/*,{
        	text: '行业', 
        	dataIndex:'en_profession',
        	renderer:function(value,metaData ,record){ 
	        	if(null != value){
	        		metaData.tdAttr = 'data-qtip="'+ this.gettips(this,record) +'"';  
	        		return value;  
	           	}else return null;   
	        }
	    }*/,{
	    	text: '邮箱',
	    	dataIndex:'en_email',
	    	width:100,
	    	renderer:function(value,metaData ,record){ 
	        	if(null != value){
	        		metaData.tdAttr = 'data-qtip="'+ this.gettips(this,record) +'"';  
	        		return value;  
	           	}else return null;   
	        }
	    },{
	    	text: '联系人',
	    	width:70,
	    	dataIndex:'en_contactman',
	    	renderer:function(value,metaData ,record){ 
	        	if(null != value){
	        		metaData.tdAttr = 'data-qtip="'+ this.gettips(this,record) +'"';  
	        		return value;  
	           	}else return null;   
	        }
	    }/*,{
	    	text: '联系人电话',
	    	dataIndex:'en_contacttel',
	    	renderer:function(value,metaData ,record){ 
	        	if(null != value){
	        		metaData.tdAttr = 'data-qtip="'+ this.gettips(this,record) +'"';  
	        		return value;  
	           	}else return null;   
	        }
	    }*//*,{
        	text:'可询价数',
        	width:100,
        	dataIndex:'hitNums',
	        renderer:function(value,metaData ,record){ 
	        	if(null != value){
	        		metaData.tdAttr = 'data-qtip="'+ this.gettips(this,record) +'"';  
	        		return value;  
	           	}else return null;   
	        } 
        }*/,{
	    	text:'器件信息',
	    	width:300,
	    	  resizable:false,
	    	dataIndex:'productInfo',
	    	renderer: function (value,metaData,record) {
	    		var data = record.data;
	        	if(data&&value!=null&&value!=""){
	        		metaData.tdAttr = 'data-qtip="'+ value.replace(/\"/g,'\'') +'"'; 
	        		return "<div class='productDetail'> <a  href='#' " +
	        				"onclick = showProductDetail('"+record.index+"') >"+value+"</a></div> " ;
	        	}else{
    				/*return " <div><a class=\"btn-search\" href='#' " +
    					"onclick = showProductDetail('"+record.index+"') type='button'>详情</a><div>    ";*/
	        		return "";
	        	}
	        }
        },{
          text: '操作', 
          width:70,
          resizable:false,
          align: 'center',
          renderer: function (value,metaData,record) {
          	if(record.data&&record.data.isVendor == null ){
          		metaData.tdAttr = 'data-qtip="引进"'; 
				return "<a class='btn btn-primary importVendor' href='#' onclick = importVendor('"+record.index+"') type='button'>引进</a>";
          	}else{
          		return "";
          	}
          }
        }	
    
    ],
    frame:true,
    forceFit: true,
    sortable: true,
	columnLines : true,
	autoScroll : true, 
	//sync: true,
	bodyStyle: 'background-color:#f1f1f1;',
	bbar: Ext.create('Ext.PagingToolbar', {   
            store: vendorProdStore,   
            displayInfo: true,   
            displayMsg: '显示 {0} - {1} 条，共计 {2} 条',   
            emptyMsg: "没有数据"   
          }) 
    ,listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
		afterrender:function(){
			var panel = parent.Ext.getCmp('tree-tab');
			if(panel && !panel.collapsed) {
				panel.toggleCollapse();
			}else{
				panel = parent.parent.Ext.getCmp('tree-tab');
				if(panel && !panel.collapsed) {
					panel.toggleCollapse();
				}	
			}
			if(parent.Ext.getCmp('win')){
				parent.Ext.getCmp('win').maximize();
			}
		},
		itemmouseenter :function(me,record,item,index,e,op){
			//Ext.getCmp('erpVendorImportGridPanel').showtips(me,record);
		}
	},
	gettips:function(me,record){
		var tipMsg = '<div><p><b>企业名：</b>' + record.get('en_name') + '</p>' +
        		'<p><b>企业简称：</b>'+ me.ifnull(record.get('en_shortname')) + '</p>' +
        		'<p><b>营业执照号：</b>'+me.ifnull(record.get('en_businesscode')) + '</p>' +
        		'<p><b>行业：</b>'+ me.ifnull(record.get('en_profession')) + '</p>' +
        		/*'<p><b>区域：</b>'+ me.ifnull(record.get('en_tags')) + '</p>' +*/
        		'<p><b>电话：</b>'+ me.ifnull(record.get('en_tel')) + '</p>' +
        		'<p><b>邮箱：</b>'+ me.ifnull(record.get('en_email')) + '</p>' +
        		'<p><b>地址：</b>'+ me.ifnull(record.get('en_address')) + '</p>' +
        		'<p><b>联系人：</b>'+ me.ifnull(record.get('en_contactman')) + '</p>' +
        		'<p><b>联系人电话：</b>'+ me.ifnull(record.get('en_contacttel')) + '</p>' +
        		//'<p><b>可买物料数：</b>'+ me.ifnull(record.get('hitNums')) + '</p>' +
        		'<p><b>器件信息：</b>'+ me.ifnull(record.get('productInfo')) + '</p></div>' ;
        		return tipMsg;
	},
	showtips:function(me,record){
		me.tip = Ext.create('Ext.tip.ToolTip', {
		        target: me.el,
		        delegate: me.itemSelector,
		        trackMouse: true,
		        renderTo: Ext.getBody(),
		        listeners: {
		            beforeshow: function updateTipBody(tip) {
		            	var record = me.getRecord(tip.triggerElement);
		            	var grid = Ext.getCmp('erpVendorImportGridPanel');
		                tip.update('<div class="vendorDetailTip"><p><b>企业名：</b>' + record.get('en_name') + '</p>' +
		                		'<p><b>企业简称：</b>'+ grid.ifnull(record.get('en_shortname')) + '</p>' +
		                		'<p><b>营业执照号：</b>'+grid.ifnull(record.get('en_businesscode')) + '</p>' +
		                		'<p><b>电话：</b>'+ grid.ifnull(record.get('en_tel')) + '</p>' +
		                		'<p><b>邮箱：</b>'+ grid.ifnull(record.get('en_email')) + '</p>' +
		                		'<p><b>地址：</b>'+ grid.ifnull(record.get('en_address')) + '</p>' +
		                		'<p><b>行业：</b>'+ grid.ifnull(record.get('en_profession')) + '</p>' +
		                		'<p><b>业务范围：</b>'+ grid.ifnull(record.get('en_tags')) + '</p>' +
		                		'<p><b>联系人：</b>'+ grid.ifnull(record.get('en_contactman')) + '</p>' +
		                		'<p><b>联系人联系电话：</b>'+ grid.ifnull(record.get('en_contacttel')) + '</p>' +
		                		'<p><b>可买物料数：</b>'+ grid.ifnull(record.get('hitNums')) + '</p>' +
		                		'<p><b>可买物料：</b>'+ grid.ifnull(record.get('productInfo')) + '</p>' +
		                		'</div>' );
		            }
		        }
		    });
	},
	ifnull:function(a){
		if (a===''||a===null||a === undefined){
			return '不详';
		}else if(String(a).indexOf('\"')>=0||String(a).indexOf('\'')>=0){
			return a.replace(/\"/g,'\'');
		}else return a;
	},
	initComponent:function(){
		this.GridUtil = Ext.create('erp.util.GridUtil');
	    this.BaseUtil = Ext.create('erp.util.BaseUtil');
	    condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
		this.callParent(arguments);
	}
});
