package com.kc.walle.station.engine.daemon.common;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.kc.walle.common.das.IBoxDas;
import com.kc.walle.common.das.IBoxDetailDas;
import com.kc.walle.common.das.IBucketDas;
import com.kc.walle.common.das.IBucketSlotDas;
import com.kc.walle.common.das.IBucketSlotTypeDas;
import com.kc.walle.common.das.IBucketTypeDas;
import com.kc.walle.common.das.ICustomerDas;
import com.kc.walle.common.das.IDirectShelveBillDas;
import com.kc.walle.common.das.IGoodsInBillDas;
import com.kc.walle.common.das.IReplenishQuotaDas;
import com.kc.walle.common.das.IReplenishmentOrderDas;
import com.kc.walle.common.das.ISkuBatchDas;
import com.kc.walle.common.das.ISkuDas;
import com.kc.walle.common.das.IStationDas;
import com.kc.walle.common.das.IStationWaypointDas;
import com.kc.walle.common.das.IUserDas;
import com.kc.walle.common.das.IWaypointDas;
import com.kc.walle.common.infrastructure.config.WalleCommonProperties;
import com.kc.walle.common.infrastructure.message.IRequester;
import com.kc.walle.common.infrastructure.message.QueueName;
import com.kc.walle.common.infrastructure.message.activemq.ActiveMQRequesterFactory;
import com.kc.walle.common.infrastructure.service.ServiceFactory;

@Service
public class ServiceBeanFactory {

	private IUserDas userDas;
	private ICustomerDas customerDas;
	private IBucketTypeDas bucketTypeDas;
	private IStationDas stationDas;
	private IGoodsInBillDas goodsInBillDas;
	private IReplenishQuotaDas replenishQuotaDas;
	private IDirectShelveBillDas directShelveBillDas;
	private IBucketSlotTypeDas bucketSlotTypeDas;
	private IBucketDas bucketDas;
	private IBucketSlotDas bucketSlotDas;
	private IWaypointDas waypointDas;
	private IBoxDas boxDas;
	private ISkuDas skuDas;
	private ISkuBatchDas skuBatchDas;
	private IStationWaypointDas stationWaypointDas;
	private IReplenishmentOrderDas replenishOrderDas;
	private IBoxDetailDas boxDetailDas;
	
	private IRequester bucketLeaveRequester;
	private IRequester jobReporter;
	
	@PostConstruct
	public void init(){
		this.userDas = ServiceFactory.createService(IUserDas.class);
		this.customerDas = ServiceFactory.createService(ICustomerDas.class);
		this.bucketTypeDas = ServiceFactory.createService(IBucketTypeDas.class);
		this.stationDas = ServiceFactory.createService(IStationDas.class);
		this.goodsInBillDas = ServiceFactory.createService(IGoodsInBillDas.class);
		this.replenishQuotaDas = ServiceFactory.createService(IReplenishQuotaDas.class);
		this.directShelveBillDas = ServiceFactory.createService(IDirectShelveBillDas.class);
		this.bucketSlotTypeDas = ServiceFactory.createService(IBucketSlotTypeDas.class);
		this.bucketDas = ServiceFactory.createService(IBucketDas.class);
		this.bucketSlotDas = ServiceFactory.createService(IBucketSlotDas.class);
		this.waypointDas = ServiceFactory.createService(IWaypointDas.class);
		this.boxDas = ServiceFactory.createService(IBoxDas.class);
		this.skuDas = ServiceFactory.createService(ISkuDas.class);
		this.skuBatchDas = ServiceFactory.createService(ISkuBatchDas.class);
		this.stationWaypointDas = ServiceFactory.createService(IStationWaypointDas.class);
		this.replenishOrderDas = ServiceFactory.createService(IReplenishmentOrderDas.class);
		this.boxDetailDas = ServiceFactory.createService(IBoxDetailDas.class);
		
		bucketLeaveRequester = ActiveMQRequesterFactory.getInstance().createRequester(QueueName.return_bucket_to_picking_area_queue,  WalleCommonProperties.instance.getMQConnectionURL(), false);
		bucketLeaveRequester.init();
		
		jobReporter = ActiveMQRequesterFactory.getInstance().createRequester(QueueName.return_bucket_to_picking_area_queue, WalleCommonProperties.instance.getMQConnectionURL(), true);
		jobReporter.init();
	}
	
	@PreDestroy
	public void destroy() {
		if(bucketLeaveRequester != null){
			bucketLeaveRequester.destroy();
			bucketLeaveRequester = null;
		}
		if(jobReporter != null){
			jobReporter.destroy();
			jobReporter = null;
		}
	}

	public IUserDas getUserDas() {
		return userDas;
	}

	public ICustomerDas getCustomerDas() {
		return customerDas;
	}

	public IBucketTypeDas getBucketTypeDas() {
		return bucketTypeDas;
	}

	public IStationDas getStationDas() {
		return stationDas;
	}

	public IGoodsInBillDas getGoodsInBillDas() {
		return goodsInBillDas;
	}

	public IReplenishQuotaDas getReplenishQuotaDas() {
		return replenishQuotaDas;
	}

	public void setReplenishQuotaDas(IReplenishQuotaDas replenishQuotaDas) {
		this.replenishQuotaDas = replenishQuotaDas;
	}

	public IDirectShelveBillDas getDirectShelveBillDas() {
		return directShelveBillDas;
	}

	public void setDirectShelveBillDas(IDirectShelveBillDas directShelveBillDas) {
		this.directShelveBillDas = directShelveBillDas;
	}

	public IBucketSlotTypeDas getBucketSlotTypeDas() {
		return bucketSlotTypeDas;
	}

	public void setBucketSlotTypeDas(IBucketSlotTypeDas bucketSlotTypeDas) {
		this.bucketSlotTypeDas = bucketSlotTypeDas;
	}

	public IBucketDas getBucketDas() {
		return bucketDas;
	}

	public IBucketSlotDas getBucketSlotDas() {
		return bucketSlotDas;
	}

	public IWaypointDas getWaypointDas() {
		return waypointDas;
	}

	public IRequester getBucketLeaveRequester() {
		return bucketLeaveRequester;
	}

	public IBoxDas getBoxDas() {
		return boxDas;
	}

	public ISkuDas getSkuDas() {
		return skuDas;
	}

	public ISkuBatchDas getSkuBatchDas() {
		return skuBatchDas;
	}

	public IStationWaypointDas getStationWaypointDas() {
		return stationWaypointDas;
	}

	public IReplenishmentOrderDas getReplenishOrderDas() {
		return replenishOrderDas;
	}

	public IBoxDetailDas getBoxDetailDas() {
		return boxDetailDas;
	}

	public IRequester getJobReporter() {
		return jobReporter;
	}
	
}
