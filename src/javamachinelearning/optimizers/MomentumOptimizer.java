package javamachinelearning.optimizers;

import javamachinelearning.utils.Tensor;

public class MomentumOptimizer implements Optimizer{
	private double learnRate;
	private double mu; // friction to decay momentum

	public MomentumOptimizer(){
		this.learnRate = 0.1;
		this.mu = 0.9;
	}

	public MomentumOptimizer(double learnRate){
		this.learnRate = learnRate;
		this.mu = 0.9;
	}

	public MomentumOptimizer(double learnRate, double mu){
		this.learnRate = learnRate;
		this.mu = mu;
	}

	@Override
	public int extraParams(){
		return 1;
	}

	@Override
	public void update(){
		// nothing to do
	}

	@Override
	public Tensor optimize(Tensor grads, Tensor[] params){
		// parameter: velocity
		Tensor prev = params[0];
		params[0] = params[0].mul(mu).sub(grads.mul(learnRate));
		return prev.mul(mu).sub(params[0].mul(1.0 + mu)); // is negated
	}
}
