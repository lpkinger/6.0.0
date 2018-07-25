Ext.define('erp.view.plm.budget.ProjectChart',{ 
	extend: 'Ext.chart.Chart', 
	alias: 'widget.ProjectChart', 
    id: 'chartCmp',
   animate: true,
    store: Ext.create('Ext.data.Store',{
    fields: ['name', 'amount'],
    data:[]
    }),
    shadow: true,
    legend: {
         position: 'right',
        },
    insetPadding: 60,
    theme: 'Base:gradients',
    findid:null,
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	series: [{
                type: 'pie',
                field: 'amount',
                showInLegend: true,
               // donut: donut,
                tips: {
                  // trackMouse: true,
                  width: 240,
                  height: 28,
                  renderer: function(storeItem, item) {
                    var total=0;
                    Ext.getCmp('chartCmp').store.each(function(rec) {
                        total += Number(rec.get('amount'));
                    });
                    this.setTitle(storeItem.get('name') + ': ' + Math.round(storeItem.get('amount') / total * 100) + '%' + ' 金额 :￥'+storeItem.get('amount'));
                  }
                },
                highlight: {
                  segment: {
                    //margin: 20
                  }
                },
                label: {
                    field: 'name',
                    display: 'rotate',
                    contrast: true,
                    font: '14px Arial'
                }
            }],
	initComponent : function(){
	 var me=this;
	 var id=Number(me.BaseUtil.getUrlParam('formCondition').split('IS')[1]);
	 this.store.loadData(me.getChartData(id)); 
	 this.callParent(arguments);
	},
	getChartData:function(id){
	    var data=[];
	     Ext.Ajax.request({//拿到grid的columns
            	url : basePath + 'plm/budget/getData.action',
            	params:{
            	 id:this.findid||id
            	},
            	async:false,
            	method : 'post',
            	callback : function(options,success,response){
            		var res = new Ext.decode(response.responseText);
            		if(res.success){
            		data=Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            		}
            	}
        	});
	  return data;
	}
});

