var count = 0;
Ext.define('erp.view.sys.sale.CurrencyForm',{
	extend: 'Ext.form.Panel', 
	alias: 'widget.currencyform',
	id:'currencyform',
	border:false,
	load:false,
	bodyStyle : 'background:#ffffff;',
	viewConfig: {
        stripeRows: true,
        enableTextSelection: true//允许选中文字
    },
	//frame: true,
	       items:[{
	    	   xtype:'monthdatefield',
	    //	   format:'Ym',
	    	   id:'firstdate',
	    	   fieldLabel:'<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">第一步，设置系统财务开账账期</font>',
	    	   labelWidth:200,
	    	   margin:'15 0 0 40',
	    	   allowblank:true,
	    	   listeners:{
	    		   afterrender:function(){
	    			   var form = Ext.getCmp('currencyform');
	    			   Ext.Ajax.request({
	    				   url:basePath+'ma/logic/getCurrencyDate.action',
	    				   method:'post',
	    				   params:'',
	    				   callback:function(options,success,response){
	    					   var res = Ext.decode(response.responseText);
	    					   var val = res.data;
	    					   Ext.getCmp('firstdate').setValue(val);
	    				   }
	    			   });
	    		   },
	    	   },
	       },{
	    	   xtype:'tbtext',
	    	   text:'<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">第二步，确认系统财务记账币别(单选)</font></font>',
	    	   labelWidth:280,
	    	   selectOnFocus: true,
	    	   margin:'15 0 0 40'
	       },{
	    		   xtype:'radiogroup',
	    		   columns:5,
	    		   id:'syscurrency',
	    		   name:'syscurrency',
	    		   vertical:true,
	    		   margin:'10 0 0 40',
	    		   fieldDefaults:{
	    			  margin:'10 0 0 0'
	    		   },
	    		   listeners:{
	    			   change:function(me, newValue, oldValue, eOpts ){
	    				 var bs = Ext.getCmp('bscurrency');
	    				 bs.reset();
	    				 var grid = me.ownerCt.ownerCt.down('currencygrid');
	    				 Ext.getCmp('bs'+newValue.rb).setValue(true);
	    				 Ext.getCmp('bs'+newValue.rb).setDisabled(true);
	    				 if(oldValue.rb){
	    					 Ext.getCmp('bs'+oldValue.rb).setDisabled(false);
	    					 Ext.getCmp('bs'+oldValue.rb).setValue(false);
	    				 }
	    				 grid.getStore().loadData([]);
	    			   },
	    			   afterrender:function(t){
	    				   var syscurrency = Ext.getCmp('syscurrency').items.items;//本位币
		    			   var form = Ext.getCmp('currencyform');
		    			   Ext.Ajax.request({
		    				   url:basePath+'ma/logic/getSysCurrency.action', 
		    				   method:'post',
		    				   params:'',
		    				   callback:function(options,success,response){
		    					   var res = Ext.decode(response.responseText);
		    					   var val = res.data;
		    					   for(var i = 0;i<syscurrency.length;i++){
		    	    					   var sys = syscurrency[i].boxLabel.substring(syscurrency[i].boxLabel.indexOf(">")+1,syscurrency[i].boxLabel.lastIndexOf("("));
		    	    					   if(sys==val){
		    	    						   Ext.getCmp('sys'+val).setValue(true);
		    	    						   t.ownerCt.load=true;
		    	    					   }
		    	    			   }
		    				   }
		    			   });
		    		   }
	    		   },
	    		   items:[ 
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">RMB(人民币元)</font>', name: 'rb', inputValue: 'RMB',id:'sysRMB' },//; font-family:"微软雅黑";font-color=red
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">CHF(瑞士法郎)</font>', name: 'rb', inputValue: 'CHF',id:'sysCHF'},
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">HKD(港币)</font>', name: 'rb', inputValue: 'HKD',id:'sysHKD'},
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">JPY(日元)</font>', name: 'rb', inputValue: 'JPY' ,id:'sysJPY'},
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">USD(美元)</font>', name: 'rb', inputValue: 'USD' ,id:'sysUSD'},
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">CAD(加拿大元)</font>', name: 'rb', inputValue: 'CAD' ,id:'sysCAD'},
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">EUR(欧元)</font>', name: 'rb', inputValue: 'EUR' ,id:'sysEUR'},
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">TWD(台币)</font>', name: 'rb', inputValue: 'TWD' ,id:'sysTWD'},
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">GBP(英镑)</font>', name: 'rb', inputValue: 'GBP' ,id:'sysGBP'},
	    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">AUD(澳大利亚元)</font>', name: 'rb', inputValue: 'AUD' ,id:'sysAUD'}]
	       },{
	    	   xtype:'tbtext',
	    	   text:'<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">第三步，确认系统买卖使用币别(多选)</font></font>',
	    	   labelWidth:280,
	    	   selectOnFocus:true,
	    	   margin:'15 0 0 40'
	       },{
	    	   xtype:'checkboxgroup',
	    	   columns:5,
	    	   name:'bscurrency',
	    	   margin:'10 0 0 40',
	    	   fieldDefaults:{
	    		   margin:'10 0 0 0',
	    	   },
	    	   id:'bscurrency',
	    	   defaults:{
	    		     labelAlign:'left'//标签对齐方式    
	    	   },
	    	   listeners:{
	    		   change:function(f, newValue, oldValue, eOpts){
	    			   var me = this;
	    			   var bscurrency = Ext.getCmp('bscurrency').items.items;
	    			   var syscurrency = Ext.getCmp('syscurrency').items.items;//本位币
	    			   syscurrency_check=Ext.getCmp('syscurrency').getChecked( );
	    			   if(syscurrency_check.length==0&&newValue!=oldValue&&Ext.Object.getKeys(newValue).length!=0){  //限制没有选择本位币时不可勾选外币
	    				   showResult('提示','先设置系统财务记账币别，再设置系统买卖使用币别！');
	    				   for(var i = 0;i<bscurrency.length;i++){
		    				   if(bscurrency[i].checked==true){
		    					   bscurrency[i].setValue(false);  
		    				   }
		    			   }
	    				   return;
	    			   }
	    			   var currency = new Array();
	    			   for(var i = 0;i<bscurrency.length;i++){ //创建一个新的数组把勾选了的多选框的值全部push到currency数组中
	    				   if(bscurrency[i].checked==true){
	    					   var bs = bscurrency[i].boxLabel.substring(bscurrency[i].boxLabel.indexOf(">")+1,bscurrency[i].boxLabel.lastIndexOf("("));
	    					   currency.push(bs);
	    				   }
	    			   }
	    			   var sys;
	    			   for(var j=0;j<syscurrency.length;j++){ //把数组中remove掉本位币的内容，为了grid表中不显示本位币，然后判断如果勾选的本位币对应的外币取消勾选则会自动勾选，看起来像限制勾选，其实只是强行再次勾选
		    			   if(syscurrency[j].checked){
							   sys = syscurrency[j].boxLabel.substring(syscurrency[j].boxLabel.indexOf(">")+1,syscurrency[j].boxLabel.lastIndexOf("("));
							   Ext.Array.remove(currency,sys);
						   }
	    			   }
	    			   var grid = Ext.getCmp('bscurrency').ownerCt.ownerCt.down('currencygrid');
	    			   if(currency&&oldValue.rbc){ //当currency和oldValue.rbc同时存在时才进行操作，避免了oldValue类型是空时无法进行比较的问题
	    				   if(typeof(oldValue.rbc)!='object'){ //当oldValue不是object类型  即当oldValue为string类型时
	    					   Ext.each(currency,function(r){  //把新勾选的值传给newdata 再加载到下方的grid中
	    						   if(r!=oldValue.rbc){//old 只有一条
	    							   var newdata=new Array();
	    							   var fd={
	    									   CR_NAME:'',
	    									   CR_NAME:'',
	    							   }
	    							   fd.CR_NAME=currency[0];
	    							   newdata.push(fd);	 
	    							   grid.getStore().add(newdata);
	    						   }
	    					   });
	    				   }else{//当两个都为object时，用difference函数拿到新勾选的值然后给newdata加载到grid中
	    					   if(typeof(newValue.rbc)=='object'&&typeof(oldValue.rbc)=='object'){
	    						   if(newValue.rbc.length>oldValue.rbc.length){
			    					   currency = Ext.Array.difference( currency, oldValue.rbc );//勾选
			    					   var newdata=new Array();
									   var fd={
											   CR_NAME:'',
											   CR_NAME:'',
									   }
									   fd.CR_NAME=currency[0];
									   newdata.push(fd);	 
									   grid.getStore().add(newdata);
	    						   }
	    					   }
	    				   }
	    				   if(typeof(newValue.rbc)=='object'&&typeof(oldValue.rbc)=='object'){ //从grid中remove掉 取消勾选的值
	    					   if(newValue.rbc.length<oldValue.rbc.length){
	    						   currency = Ext.Array.difference(oldValue.rbc, newValue.rbc );
	    						   for(var x=0;x<grid.getStore().data.items.length;x++){
	    							   if(grid.getStore().data.items[x].data.CR_NAME==currency){
	    								   grid.getStore().remove(grid.getStore().data.items[x]);
	    							   }
	    						   }
	    					   }
	    				   }else if(typeof(newValue.rbc)=='string'&&typeof(oldValue.rbc)=='object'){
	    					   currency = Ext.Array.remove(oldValue.rbc,newValue.rbc);
    						   for(var x=0;x<grid.getStore().data.items.length;x++){
    							   if(grid.getStore().data.items[x].data.CR_NAME==currency){
    								   grid.getStore().remove(grid.getStore().data.items[x]);
    							   }
    						   } 
	    				   }
	    			   }
	    		   },
	    		   afterrender:function(f){
    				   var bscurrency = Ext.getCmp('bscurrency').items.items;//外币
	    			   var form = Ext.getCmp('currencyform');
	    			   Ext.Ajax.request({
	    				   url:basePath+'ma/logic/getBsCurrency.action', 
	    				   method:'post',
	    				   params:'',
	    				   callback:function(options,success,response){
	    					   var res = Ext.decode(response.responseText);
	    					   var grid = Ext.getCmp('bscurrency').ownerCt.ownerCt.down('currencygrid');
	    					   var val = res.data;
	    					   for(var i = 0;i<bscurrency.length;i++){
	    	    					   var bs = bscurrency[i].boxLabel.substring(bscurrency[i].boxLabel.indexOf(">")+1,bscurrency[i].boxLabel.lastIndexOf("("));
	    	    					   for(var j = 0 ; j<val.length;j++){
		    	    					   if(bs==val[j]['CR_NAME']){
		    	    						   if(f.ownerCt.load==true){
		    	    							   Ext.getCmp('bs'+val[j]['CR_NAME']).setValue(true);
		    	    						   }
		    	    					   }
	    	    					   }
	    	    			   }
	    					   grid.getStore().loadData(val); 
	    				   }
	    			   });
	    		   }
	    	   },	    	   
	    	   items:[ 
	    	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">RMB(人民币元)</font></font>', name: 'rbc', inputValue: 'RMB',id:'bsRMB' },
	    	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">HKD(港币)</font></font>', name: 'rbc', inputValue: 'HKD',id:'bsHKD'},
	    	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">USD(美元)</font></font>', name: 'rbc', inputValue: 'USD' ,id:'bsUSD'},
	    	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">EUR(欧元)</font></font>', name: 'rbc', inputValue: 'EUR' ,id:'bsEUR'},
	    	            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">GBP(英镑)</font></font>', name: 'rbc', inputValue: 'GBP' ,id:'bsGBP'},
    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">CHF(瑞士法郎)</font></font>', name: 'rbc', inputValue: 'CHF',id:'bsCHF'},
    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">JPY(日元)</font></font>', name: 'rbc', inputValue: 'JPY' ,id:'bsJPY'},
    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">CAD(加拿大元)</font></font>', name: 'rbc', inputValue: 'CAD' ,id:'bsCAD'},
    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">TWD(台币)</font></font>', name: 'rbc', inputValue: 'TWD' ,id:'bsTWD'},
    		            { boxLabel: '<font size="2" style="font-family:Microsoft YaHei">AUD(澳大利亚元)</font></font>', name: 'rbc', inputValue: 'AUD' ,id:'bsAUD'}]
	       },{
	    	   xtype:'tbtext',
	    	   text:'<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">第四步，设置系统买卖币别对本位币汇率<font size=2px style="font-family:黑体;color:rgba(0,0,0,0.85)">(系统中买卖使用币别的汇率月初默认为上月汇率；如汇率波动比较大时，请重新设置)</font></font>',
	    	   labelWidth:280,
	    	   selectOnFocus:true,
	    	   margin:'15 0 10 40'
	       }],
	       store:Ext.create('Ext.data.Store',{
				fields:[],
				 proxy: {
				        type: 'memory'
				    }
	       }),
	initComponent : function(){
		this.callParent(arguments);
	}
});