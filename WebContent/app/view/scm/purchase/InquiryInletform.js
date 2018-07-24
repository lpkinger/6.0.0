Ext.define('erp.view.scm.purchase.InquiryInletform',{
	extend: 'Ext.form.Panel', 
	alias: 'widget.inquiryinletform',
	id:'inquiryinletform',
	border:false,
	load:false,
	frame :true,
	autoScroll:true,
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:['core.trigger.TextAreaTrigger','core.form.FileField'],
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	viewConfig: {
        stripeRows: true,
        enableTextSelection: true//允许选中文字
    },
    items:[{
    	 xtype:'radiogroup',
		   columns:5,
		   fieldLabel: '<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">询价范围</font></font>',
		   id:'type',
		   name:'type',
		   vertical:true,
		   value:'外部询价',
		   margin:'10 0 0 40',
		   fieldDefaults:{
			  margin:'0 0 0 0'
		   },
		   items:[ 
		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">外部询价</font>', name: 'rb', inputValue: '外部询价',id:'sysWB' },//; font-family:"微软雅黑";font-color=red
		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">内部询价</font>', name: 'rb', inputValue: '内部询价',id:'sysNB'},
		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">指定供应商</font>', name: 'rb', inputValue: '指定供应商',id:'sysGYS'}],
           listeners:{
        	   beforerender:function(){
        		   Ext.getCmp('vendtab').tab.hide();
        		   Ext.getCmp('kindtab').tab.hide();
        	   },
        	   change:function(e,newVal,oldVal){
				   if(newVal.rb == '外部询价' || newVal.rb == '内部询价'){
						Ext.getCmp('vendtab').tab.hide();
	        			var tab = Ext.getCmp('tab');
	        			if(tab.getActiveTab().id!='prodtab'){
	        				tab.setActiveTab('prodtab');
	        			}
					}else if (newVal.rb == '指定供应商'){
						Ext.getCmp('vendtab').tab.show();
	        			Ext.getCmp('tab').setActiveTab('vendtab');
					}
			   },
			   afterrender:function(){
				   Ext.getCmp('sysWB').setValue(true);
			   }
		   }
    },{
   	 xtype:'radiogroup',
	   columns:5,
	   fieldLabel: '<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">询价对象</font></font>',
	   id:'prod',
	   name:'prod',
	   vertical:true,
	   margin:'10 0 0 40',
	   fieldDefaults:{
		  margin:'0 0 0 0'
	   },
	   items:[ 
	          	{ boxLabel: '<font size="2" style="font-family:Microsoft YaHei">按具体物料</font>', name: 'rbc', inputValue: '按具体物料',id:'sysProd'},
	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">按物料种类</font>', name: 'rbc', inputValue: '按物料种类',id:'sysProdKind'},
	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei" onClick=getBomWin()>按BOM物料</font>', name: 'rbc', inputValue: '按BOM物料',id:'sysBom'},
	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">我负责的物料</font>', name: 'rbc', inputValue: '我负责的物料',id:'sysAll' }],
       listeners:{
    	   change:function(e,newVal,oldVal){
    		   if(newVal.rbc=='按物料种类'){
    			   Ext.getCmp('prodtab').tab.hide();
    			   Ext.getCmp('kindtab').tab.show();
    			   Ext.getCmp('tab').setActiveTab('kindtab');
    			   Ext.getCmp('prodtab').GridUtil.add10EmptyItems(Ext.getCmp('prodtab'));
    		   }else if(newVal.rbc=='我负责的物料'){
    			   var grid = Ext.getCmp('prodtab');
    			   Ext.getCmp('kindtab').tab.hide();
    			   Ext.getCmp('prodtab').tab.show();
    			   Ext.getCmp('tab').setActiveTab('prodtab');
    			   var param = {caller:caller,condition:''};
    			   Ext.Ajax.request({//拿到grid的columns
    		        	url : basePath + "scm/purchase/getAllPurc.action",
    		        	params: param,
    		        	method : 'post',
    		        	callback : function(options,success,response){
    		        		grid.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		var data = res.data;
    		        		if(!data || data.length == 0){
    		        			grid.store.removeAll();
    		        			showError('物料资料中不存在采购员是当前用户的物料,请选择其它询价对象');
    		        		} else {
    		        			if(grid.buffered) {
    		        				var ln = data.length, records = [], i = 0;
    		        			    for (; i < ln; i++) {
    		        			        records.push(Ext.create(grid.store.model.getName(), data[i]));
    		        			    }
    		        			    grid.store.purgeRecords();
    		        			    grid.store.cacheRecords(records);
    		        			    grid.store.totalCount = ln;
    		        			    grid.store.guaranteedStart = -1;
    		        			    grid.store.guaranteedEnd = -1;
    		        			    var a = grid.store.pageSize - 1;
    		        			    a = a > ln - 1 ? ln - 1 : a;
    		        			    grid.store.guaranteeRange(0, a);
    		        			} else {
    		        				grid.store.loadData(data);
    		        			}
    		        		}
    		        		//自定义event
    		        		grid.addEvents({
    		        		    storeloaded: true
    		        		});
    		        		grid.fireEvent('storeloaded', grid, data);
    		        	}
    		        });
    		   }else if(newVal.rbc=='按BOM物料'){
    			   var me = this;
    			   Ext.getCmp('prodtab').store.removeAll();
    			   Ext.getCmp('kindtab').tab.hide();
    			   Ext.getCmp('prodtab').tab.show();
    			   Ext.getCmp('tab').setActiveTab('prodtab');
    			   Ext.getCmp('prodtab').GridUtil.add10EmptyItems(Ext.getCmp('prodtab'));
    			   if(!Ext.getCmp('win')){
    				   me.ownerCt.getWin();
    			   }
    		   }else if(newVal.rbc=='按具体物料'){
    			   Ext.getCmp('prodtab').store.removeAll();
    			   Ext.getCmp('kindtab').tab.hide();
    			   Ext.getCmp('prodtab').tab.show();
    			   Ext.getCmp('tab').setActiveTab('prodtab');
    			   Ext.getCmp('prodtab').GridUtil.add10EmptyItems(Ext.getCmp('prodtab'));
    		   }
    	   },
    	   afterrender:function(t){
    		   Ext.getCmp('sysProd').setValue(true);
		   }
       }
    },{
   	   xtype:'radiogroup',
	   columns:5,
	   fieldLabel: '<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">价格种类</font></font>',
	   id:'pricetype',
	   name:'pricetype',
	   vertical:true,
	   value:'标准',
	   margin:'10 0 0 40',
	   fieldDefaults:{
		  margin:'0 0 0 0'
	   },
	   items:[ 
	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">标准</font>', name: 'jb', inputValue: '标准',id:'sysBZ' },//; font-family:"微软雅黑";font-color=red
	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">临时</font>', name: 'jb', inputValue: '临时',id:'sysLS'}],
     listeners:{
		   afterrender:function(){
			   Ext.getCmp('sysBZ').setValue(true);
		   }
	   }
    },{
       xtype:'radiogroup',
  	   columns:5,
  	   fieldLabel: '<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">价格类型</font></font>',
  	   id:'pricekind',
  	   name:'pricekind',
  	   vertical:true,
  	   value:'采购',
  	   margin:'10 0 0 40',
  	   fieldDefaults:{
  		  margin:'0 0 0 0'
  	   },
  	   items:[ 
  	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">采购</font>', name: 'gb', inputValue: '采购',id:'sysCG' },//; font-family:"微软雅黑";font-color=red
  	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">委外</font>', name: 'gb', inputValue: '委外',id:'sysWW'}],
       listeners:{
  		   afterrender:function(){
  			   Ext.getCmp('sysCG').setValue(true);
  		   }
  	   }
  },{
	   xtype:'numberfield',
	   fieldLabel:'<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">询价截止日期</font></font>',
	   labelWidth:100,
	   id:'iqdate',
	   selectOnFocus: true,
	   allowBlank: false,
	   allowDecimals:false,
       hideTrigger:true,
	   minValue: 1,
	   value:7,
	   margin:'15 0 0 40'
   },{
	   xtype:'displayfield',
	   fieldLabel:'<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">默认设置为自动询价方式</font></font>',
	   labelWidth:280,
	   id:'autoiq',
	   margin:'15 0 0 40',
   },{
	   xtype: 'fieldcontainer',
       fieldLabel: '设置为每隔',
       labelWidth: 100,
       margin:'15 0 0 40',
       layout: 'hbox',
       items: [{
           xtype: 'numberfield',
           labelWidth: 30,
           width:50,
           id:'ts',
           value:1,
           allowDecimals:false,
           hideTrigger:true,
           maxValue: 12,
           minValue: 1
       }, {
    	   xtype:'combo',
           valueField:'value',
           displayField:'display',
           fieldLabel:'',
           width:50,
           margin:'0 0 0 5',
           editable:false,
           id:'xjms',
           value:'月',
           store:Ext.create('Ext.data.Store',{	       	
           	fields:['display','value'],
           	data:[
   		      {display:'天',value:'天'},
   		      {display:'月',value:'月'}]
           }),
           listeners :{
           	change:function(e,newValue,oldValue){
           		if(newValue=='月' && oldValue=='天'){
           			Ext.getCmp('ts').setMaxValue(12);
           			Ext.getCmp('ts').setValue(1);
           		}else if(newValue=='天' && oldValue=='月'){
           			Ext.getCmp('ts').setMaxValue(365);
           			Ext.getCmp('ts').setValue(30);
           		}
           	}
          }
       },{
    	   xtype:'displayfield',
    	   fieldLabel:'自动发出询价单,起始询价日期为',
    	   labelWidth:210
       },{
    	   xtype:'datefield',
    	   labelWidth:100,
    	   allowBlank: false,
    	   id:'jtiq',
    	   hidden:false,
    	   value: new Date(),
    	   margin:'0 0 0 5'
       }]
   },{
        xtype : 'textareatrigger',
        grow : true,
        id:'remark',
        name : 'remark',
        width:590,
        fieldLabel: '<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">备注</font></font>',
        margin:'15 0 0 40'
   },{
	   xtype: 'mfilefield',
       name: 'fujian',
       id:'fujian',
       fieldLabel: '附件',
       labelWidth: 50,
       width:590,
       msgTarget: 'side',
       buttonText: 'Select Photo...',
       margin:'15 0 0 40'
   },{
	    xtype: 'button',
		id : 'startIQ',
		text: '发起询价',
		formBind: true,
		cls: 'x-btn-gray',
		iconCls : 'x-button-icon-submit',
		style: {
   		marginLeft: '10px'
       },
		width: 90,
		margin:'25 0 0 40',
		handler:function(btn){
			btn.ownerCt.beforeStart();
		}
   }],
   store:Ext.create('Ext.data.Store',{
		fields:[],
		 proxy: {
		        type: 'memory'
		    }
  }),
  beforeStart:function(){
		var form = {};
		var me = Ext.getCmp('form');
		if(!Ext.getCmp('type').getValue().rb){
			showError('请选择询价范围');
			return;
		}
		if(!Ext.getCmp('prod').getValue().rbc){
			showError('请选择询价对象');
			return;
		}
		if(!Ext.getCmp('pricetype').getValue().jb){
			showError('请选择价格种类');
			return;
		}
		if(!Ext.getCmp('pricekind').getValue().gb){
			showError('请选择价格类型');
			return;
		}
		if(!Ext.getCmp('ts').getValue()){
			showError('请输入询价周期');
			return;
		}
		form['fw'] = Ext.getCmp('type').getValue().rb;
		form['dx'] = Ext.getCmp('prod').getValue().rbc;
		form['zl'] = Ext.getCmp('pricetype').getValue().jb;
		form['lx'] = Ext.getCmp('pricekind').getValue().gb;
		form['rq'] = Ext.getCmp('iqdate').getValue();
		form['ms'] = Ext.getCmp('xjms').getValue();
		form['dt'] = Ext.getCmp('ts').getValue();
		form['bz'] = Ext.getCmp('remark').getValue();
		form['fj'] = Ext.getCmp('fujian').getValue();
		form['jt'] = Ext.Date.format(Ext.getCmp('jtiq').getValue(),'Y-m-d');
		var grid1 = Ext.getCmp('prodtab');
		var grid2 = Ext.getCmp('vendtab');
		var grid3 = Ext.getCmp('kindtab');
		var param1 = new Array();
		if(grid1){
			param1 = me.GridUtil.getAllGridStore(grid1);
		}
		
		var param2 = new Array();
		if(grid2){
			param2 = me.GridUtil.getGridStore(grid2);
		}
		
		var param3 = new Array();
		if(grid3){
			param3 = me.GridUtil.getGridStore(grid3);
		}
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
		me.start(form,param1,param2,param3);
  },
  start:function(form,param1,param2,param3){
	  var params = new Object();
	  var me = this ;
	  var r = arguments[0];
	  params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
	  params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
	  params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
	  params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
	  me.setLoading(true);//loading...
	  Ext.Ajax.request({
	   		url : basePath + 'scm/purchase/inquiryInlet.action',
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	   			
   			me.setLoading(false);
   			var localJson = new Ext.decode(response.responseText);
  			if(localJson.success){
  				if(localJson.log){
					showMessage("提示", localJson.log);
					window.location.reload();
				}
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					showError(str);
	   					window.location.reload();
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} 
	   		}
		});
  },
  getWin:function(){
	  var url='jsps/common/batchDealer.jsp?whoami=BOMStruct!IQ&urlcondition=1=1';
	  if(!Ext.getCmp('win')){
	  var win = Ext.create('Ext.window.Window',{
			title: '<span style="color:#CD6839;">BOM</span>',
			iconCls: 'x-button-icon-set',
			height: "100%",
			width: "80%",
			id:'win',
			maximizable : true,
			buttonAlign : 'center',
			layout : 'fit',
			items : [{    
				header:false, 
				id:'iqgrid',
				html : '<iframe src="'+basePath+url+'"  id="setframe" name="setframe" width="100%" height="100%"></iframe>', 
				border:false 
			}],
			buttonAlign:'center',
			buttons:[{
				xtype: 'button',
				id : 'confirm',
				text: $I18N.common.button.erpConfirmButton,
				cls: 'x-btn-gray',
				style: {
		    		marginLeft: '10px'
		        },
				width: 60,
				handler:function(btn){
					var contentwindow = Ext.getCmp('win').body.dom.getElementsByTagName('iframe')[0].contentWindow;
		    		var batchgrid = contentwindow.Ext.getCmp('batchDealerGridPanel');
//		    		var items = batchgrid.getMultiSelected();
		    		var items = batchgrid.selectObject;
		    		var arr = new Array();
		    		Ext.each(Ext.Object.getKeys(items), function(k){
		    			arr.push(items[k]);        
		    		});
		    		if(arr.length==0){
		    			showError("未勾选明细行数据，请选择明细行数据后确认!");
		    			return;
		    		}
//		    		Ext.each(items, function(item, index){
//			        	if(this.data[batchgrid.keyField] != null && this.data[batchgrid.keyField] != ''
//			        		&& this.data[batchgrid.keyField] != '0' && this.data[batchgrid.keyField] != 0){
//		        			item.index = this.data[batchgrid.keyField];
//		        			arr.push(item.data);        
//			        	}
//			        });
		    		var data = arr;
//		    		var singledata;
		    		var grid = Ext.getCmp('prodtab');
//		    		var param = data == null ? [] : "[" + data.toString().replace(/\\/g,"%") + "]";
		    		var param = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
		    		var params = new Object();
		    		params.caller = caller;
		    		params.param = param;
		    		Ext.Ajax.request({//拿到grid的columns
    		        	url : basePath + "scm/purchase/getBom.action",
    		        	params: params,
    		        	method : 'post',
    		        	callback : function(options,success,response){
    		        		grid.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		var data = res.data;
    		        		if(!data || data.length == 0){
    		        			grid.store.removeAll();
    		        			Ext.getCmp('prodtab').GridUtil.add10EmptyItems(Ext.getCmp('prodtab'));
    		        		} else {
    		        			if(grid.buffered) {
    		        				var ln = data.length, records = [], i = 0;
    		        			    for (; i < ln; i++) {
    		        			        records.push(Ext.create(grid.store.model.getName(), data[i]));
    		        			    }
    		        			    grid.store.purgeRecords();
    		        			    grid.store.cacheRecords(records);
    		        			    grid.store.totalCount = ln;
    		        			    grid.store.guaranteedStart = -1;
    		        			    grid.store.guaranteedEnd = -1;
    		        			    var a = grid.store.pageSize - 1;
    		        			    a = a > ln - 1 ? ln - 1 : a;
    		        			    grid.store.guaranteeRange(0, a);
    		        			} else {
    		        				grid.store.loadData(data);
    		        			}
    		        		}
    		        		//自定义event
    		        		grid.addEvents({
    		        		    storeloaded: true
    		        		});
    		        		grid.fireEvent('storeloaded', grid, data);
    		        	}
    		        });
//		    		var count = detailgrid.store.data.items.length;
//		    		var length = count;
//		    		var m=0;
//		    		for(i=0;i<data.length;i++){
//		    			dataLength = detailgrid.store.data.length;
//		    			detailgrid.store.insert(dataLength+1,{});
//		    			detailgrid.store.data.items[dataLength].set(detailgrid.columns[0].dataIndex,dataLength+1);//明细行自动编号
//		    			singledata = data[i].data;
//		    			if(i==0){
//		    				for(j=0;j<dataLength+1;j++){
//			    				if(detailgrid.store.data.items[j].data.ip_prodcode ==''||detailgrid.store.data.items[j].data.ip_prodcode ==null){
//		    			    	    detailgrid.store.data.items[j].set('ip_prodcode',singledata.bs_soncode);
//					    			detailgrid.store.data.items[j].set('ip_prodname',singledata.pr_detail);
//					    			detailgrid.store.data.items[j].set('ip_spec',singledata.pr_spec);
//					    			detailgrid.store.data.items[j].set('ip_brand',singledata.pr_brand);
//					    			detailgrid.store.data.items[j].set('ip_unit',singledata.pr_unit);
//					    			detailgrid.store.data.items[j].set('ip_orispeccode',singledata.pr_orispeccode);
//	   				    			m=1;
//	   				    			break;
//			    				}
//			    			}
//		    			}
//		    			if(m==1){
//		    				detailgrid.store.data.items[j].set('ip_prodcode',singledata.bs_soncode);
//			    			detailgrid.store.data.items[j].set('ip_prodname',singledata.pr_detail);
//			    			detailgrid.store.data.items[j].set('ip_spec',singledata.pr_spec);
//			    			detailgrid.store.data.items[j].set('ip_brand',singledata.pr_brand);
//			    			detailgrid.store.data.items[j].set('ip_unit',singledata.pr_unit);
//			    			detailgrid.store.data.items[j].set('ip_orispeccode',singledata.pr_orispeccode);
//			    			j++;
//		    			}
//		    			if(m==0){
//		    				detailgrid.store.data.items[j].set('ip_prodcode',singledata.bs_soncode);
//			    			detailgrid.store.data.items[j].set('ip_prodname',singledata.pr_detail);
//			    			detailgrid.store.data.items[j].set('ip_spec',singledata.pr_spec);
//			    			detailgrid.store.data.items[j].set('ip_brand',singledata.pr_brand);
//			    			detailgrid.store.data.items[j].set('ip_unit',singledata.pr_unit);
//			    			detailgrid.store.data.items[j].set('ip_orispeccode',singledata.pr_orispeccode);
//			    			length++;
//		    			}
//		    		}
		    		Ext.getCmp('win').hide();
		    	}
			},{
				xtype: 'button',
				id : 'blankAll',
				text: $I18N.common.button.erpCloseButton,
				cls: 'x-btn-gray',
				width: 80,
				style: {
		    		marginLeft: '10px'
		        },
		        handler:function(btn){
		        	btn.up('window').close();
	 			}
			}]
		});
  	}
	  Ext.getCmp('win').show();
  },
  initComponent : function(){
		this.callParent(arguments);
	}
});