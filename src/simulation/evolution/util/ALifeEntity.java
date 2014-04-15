package simulation.evolution.util;

import com.jme3.ai.agents.Agent;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author Tihomir Radosavljević
 */
public class ALifeEntity {

    private float foodAmount;
    private float maxFoodAmount;
    private float sexDeprivedAmount;
    private float lifeSpan;
    private float timeLeft;
    private ColorRGBA gender;
    private float hotness;
    private float happiness;
    private float eatPerTime;
    private Agent sexualPartner;
    private Agent agent;

    public ALifeEntity(Agent agent, float lifeSpan, float maxFoodAmount, float hotness, float eatPerTime, ColorRGBA gender) {
        this.foodAmount = maxFoodAmount;
        this.maxFoodAmount = maxFoodAmount;
        this.lifeSpan = lifeSpan;
        this.eatPerTime = eatPerTime;
        this.gender = gender;
        this.hotness = hotness;
        sexDeprivedAmount = 0;
        timeLeft = lifeSpan;
        happiness = 1000f;
        this.agent = agent;
    }

    public boolean isHorny() {
        return sexDeprivedAmount > 0.5;
    }

    public boolean isHungry() {
        return foodAmount < maxFoodAmount / 2;
    }

    public boolean isReallyHungry() {
        return foodAmount <= 0;
    }

    public boolean isReallyHorny() {
        return sexDeprivedAmount > 1;
    }

    public void increaseHappiness(float amount) {
        happiness += amount;
    }

    public void decreaseHappiness(float amount) {
        happiness -= amount;
    }

    public boolean isSameGender(Agent agent) {
        if (gender.equals(((ALifeEntity) agent.getModel()).getGender())) {
            return true;
        }
        return false;
    }

    public float getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(float lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(float timeLeft) {
        this.timeLeft = timeLeft;
    }

    public ColorRGBA getGender() {
        return gender;
    }

    public void setGender(ColorRGBA gender) {
        this.gender = gender;
    }

    public float getHotness() {
        return hotness;
    }

    public boolean isUnhappy() {
        return happiness <= 0;
    }

    public void increaseFoodAmount(float tpf) {
        foodAmount += eatPerTime * tpf;
        if (foodAmount > maxFoodAmount) {
            foodAmount = maxFoodAmount;
        }
    }

    public void decreaseFoodAmount(float amount) {
        foodAmount -= amount;
        if (isReallyHungry()) {
            foodAmount = 0;
        }
    }

    public boolean fullStomach() {
        return foodAmount == 1;
    }

    public float getHappiness() {
        return happiness;
    }

    public void setHappiness(float happiness) {
        this.happiness = happiness;
    }

    public float getEatPerTime() {
        return eatPerTime;
    }

    public void setEatPerTime(float eatPerTime) {
        this.eatPerTime = eatPerTime;
    }

    public Agent getSexualPartner() {
        return sexualPartner;
    }

    public void setSexualPartner(Agent sexualPartner) {
        this.sexualPartner = sexualPartner;
    }

    public void increaseSexDeprivation(float amount) {
        sexDeprivedAmount += amount;
    }

    public void decreaseSexDeprivation(float amount) {
        sexDeprivedAmount -= amount;
    }

    public void age(float tpf) {
        timeLeft -= tpf;
    }

    public boolean timeToDie() {
        return timeLeft <= 0;
    }

    public boolean hasSexualPartner() {
        return sexualPartner != null;
    }

    @Override
    public String toString() {
        String s = "Name: " + agent.getName() + "\n";
        s += "Mass: " + agent.getMass() + '\n';
        s += "HitPoints: " + agent.getHitPoint() + "\n";
        s += "Move speed: " + agent.getMoveSpeed() + "\n";
        s += "Visibility range: " + agent.getVisibilityRange() + "\n";
        s += "Eat per time: " + eatPerTime + "\n";
        s += "Happiness: " + happiness + "\n";
        s += "Hotness: " + hotness + "\n";
        s += "Food amount: " + foodAmount + "\n";
        s += "Sex deprived amount: " + sexDeprivedAmount + "\n";
        return s;
    }
}
