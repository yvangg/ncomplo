package org.eleventhlabs.ncomplo.business.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eleventhlabs.ncomplo.business.entities.Bet;
import org.eleventhlabs.ncomplo.business.entities.BetDefinition;
import org.eleventhlabs.ncomplo.business.entities.BetResults;
import org.eleventhlabs.ncomplo.business.entities.Match;
import org.eleventhlabs.ncomplo.business.entities.Round;
import org.eleventhlabs.ncomplo.business.entities.Scoreboard;
import org.eleventhlabs.ncomplo.business.entities.repositories.BetRepository;
import org.eleventhlabs.ncomplo.business.entities.repositories.MatchRepository;
import org.eleventhlabs.ncomplo.business.util.IterableUtils;
import org.javaruntype.type.Types;
import org.op4j.Op;
import org.op4j.functions.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BetService {
    

    @Autowired
    private BetRepository betRepository;
    
    @Autowired
    private MatchRepository matchRepository;

    
    private static final Map<Round,Round> previousRound = new LinkedHashMap<Round, Round>();
    
    
    static {
//        previousRound.put(Round.ROUND_OF_16, Round.GROUP_STAGE);
//        previousRound.put(Round.QUARTER_FINALS, Round.ROUND_OF_16);
//        previousRound.put(Round.SEMI_FINALS, Round.QUARTER_FINALS);
//        previousRound.put(Round.FINAL, Round.SEMI_FINALS);
    }
    
    
    public BetService() {
        super();
    }
    
    

    @Transactional
    public List<String> findAllBetOwnerNames() {
        final List<Bet> allBets = IterableUtils.toList(this.betRepository.findAll());
        return Op.on(allBets).map(Get.s("ownerName")).get();
    }
    
    
    @Transactional
    public List<String> findAllBetOwnerAccountIds() {
        final List<Bet> allBets = IterableUtils.toList(this.betRepository.findAll());
        return Op.on(allBets).map(Get.s("ownerAccountId")).get();
    }
    
    
    
    @Transactional
    public Map<Match,Bet> findAllBetsForOwner(final String ownerName) {
        final List<Bet> bets = this.betRepository.findByOwnerName(ownerName);
        return Op.on(bets).zipKeysBy(Get.obj(Types.forClass(Match.class),"match")).get();
    }
    
    
    
    @Transactional
    public List<Bet> setBets(final String ownerName, final String ownerAccountId, final List<BetDefinition> betDefinitions) {

        // Trying to use a "delete from Bet where ..." query on CloudFoundry
        // returned an error ("Not supported for DML operations")
        //this.betRepository.delete(this.betRepository.findByOwnerName(ownerName));
        this.betRepository.deleteByOwnerName(ownerName);
                
        final List<Bet> bets = new ArrayList<Bet>();
        
        for (final BetDefinition betDefinition : betDefinitions) {
            
            final Bet bet = new Bet();
            
            bet.setOwnerName(ownerName);
            bet.setMatch(this.matchRepository.findOne(betDefinition.getMatchId()));
            bet.setOwnerAccountId(ownerAccountId);
            bet.setTeamA(betDefinition.getTeamA());
            bet.setTeamB(betDefinition.getTeamB());
            bet.setScoreA(betDefinition.getScoreA());
            bet.setScoreB(betDefinition.getScoreB());
            bet.setMatchWinner(betDefinition.getMatchWinner());
            
            bets.add(this.betRepository.save(bet));
            
        }
        
        return bets; 
        
    }

//    
//    
//    
//    @Transactional
//    public void deleteAllBetsForOwner(final String ownerName) {
//        this.betRepository.deleteByOwnerName(ownerName);
//    }
//
//
//    
//    
//    @Transactional
//    public BetResults computeBetResults(final String ownerName) {
//        final Map<Match,Bet> betsByMatch = findAllBetsForOwner(ownerName);
//        return computeBetResults(ownerName, betsByMatch);
//    }
//
    
    
    
//    @Transactional
//    public Scoreboard computeScoreboard() {
//        
//        final List<Bet> allBets = IterableUtils.toList(this.betRepository.findAll());
//        
//        final Map<String,List<Bet>> betsByOwner =
//                Op.on(allBets).zipAndGroupKeysBy(Get.s("ownerName")).get();
//        
//        final Map<String,Map<Match,Bet>> betsByOwnerAndMatch =
//                Op.on(betsByOwner).forEachEntry().onValue().
//                    exec(FnList.of(Types.forClass(Bet.class)).
//                            zipKeysBy(Get.obj(Types.forClass(Match.class),"match"))).get();
//        
//        final Scoreboard scoreboard = new Scoreboard();
//        for (final Map.Entry<String,Map<Match,Bet>> entry : betsByOwnerAndMatch.entrySet()) {
//            final String ownerName = entry.getKey();
//            final Map<Match,Bet> bets = entry.getValue();
//            final BetResults betResults = computeBetResults(ownerName, bets);
//            scoreboard.addScore(ownerName, betResults.getTotalPoints(), betResults.getGroupStagePoints());
//        }
//        
//        return scoreboard;
//        
//    }
//
    
    
    
//    
//    private static BetResults computeBetResults(final String ownerName, final Map<Match,Bet> betsByMatch) {
//
//        if (betsByMatch.isEmpty()) {
//            return new BetResults(ownerName, Integer.valueOf(0), Integer.valueOf(0), new ArrayList<BetResult>());
//        }
//        
//        /*
//         * Compute which teams got to each round
//         */
//        final Map<Round,Set<Team>> teamsByRound = new LinkedHashMap<Round, Set<Team>>();
//        final Map<Round,Boolean> roundTeamsDefined = new LinkedHashMap<Round, Boolean>();
//        for (final Match match : betsByMatch.keySet()) {
//            final Round round = match.getRound();
//            Set<Team> teamsForRound = teamsByRound.get(round);
//            if (teamsForRound == null) {
//                teamsForRound = new LinkedHashSet<Team>();
//                teamsByRound.put(round, teamsForRound);
//                roundTeamsDefined.put(round, Boolean.TRUE);
//            }
//            if (match.getTeamA() != null) {
//                teamsForRound.add(match.getTeamA());
//            }
//            if (match.getTeamB() != null) {
//                teamsForRound.add(match.getTeamB());
//            }
//            if (!match.isTeamsDefined()) {
//                roundTeamsDefined.put(round, Boolean.FALSE);
//            }
//        }
//        
//        /*
//         * Compute bet results
//         */
//        
//        final List<BetResult> betResults = new ArrayList<BetResult>();
//        int groupStagePoints = 0;
//        int totalPoints = 0;
//        
//        for (final Map.Entry<Match,Bet> entry : betsByMatch.entrySet()) {
//            
//            final Match match = entry.getKey();
//            final Bet bet = entry.getValue();
//            
//            final Round round = match.getRound();
//            final Calendar date = match.getDate();
//            final String name = match.getName();
//            final Team realTeamA = match.getTeamA();
//            final Team realTeamB = match.getTeamB();
//            final Integer realScoreA = match.getScoreA();
//            final Integer realScoreB = match.getScoreB();
//            final String realScore = 
//                (match.isScoreDefined()? realScoreA + " - " + realScoreB : null);
//            final MatchWinner realMatchWinner = 
//                (match.isScoreDefined()? computeMatchWinner(realScoreA, realScoreB) : null);
//            final BetType betType = match.getBetType();
//            
//            Team teamA = null;
//            Team teamB = null;
//            String score = null;
//            MatchWinner matchWinner = null;
//            Boolean closed = null;
//            Set<BetFragment> betFragments = new LinkedHashSet<BetFragment>();
//            Set<BetFragment> betWins = new LinkedHashSet<BetFragment>();
//            Set<BetFragment> betLoses = new LinkedHashSet<BetFragment>();
//            Integer points = null;
//
//            Round lastRoundWithTeamsDefined = round;
//            while (roundTeamsDefined.get(lastRoundWithTeamsDefined) == null || !roundTeamsDefined.get(lastRoundWithTeamsDefined).booleanValue()) {
//                lastRoundWithTeamsDefined = previousRound.get(lastRoundWithTeamsDefined);
//            }
//            
//            
//            switch (betType) {
//            
//                case RESULT_AND_SCORE:
//                    teamA = realTeamA;
//                    teamB = realTeamB;
//                    Integer scoreA = bet.getScoreA();
//                    Integer scoreB = bet.getScoreB();
//                    score = scoreA + " - " + scoreB;
//                    matchWinner = computeMatchWinner(scoreA, scoreB);
//                    betFragments.add(BetFragment.MATCH_WINNER);
//                    betFragments.add(BetFragment.SCORE);
//                    if (match.isScoreDefined()) {
//                        closed = Boolean.TRUE;
//                        int p = 0;
//                        if (matchWinner.equals(realMatchWinner)) {
//                            betWins.add(BetFragment.MATCH_WINNER);
//                            p += 2;
//                        } else {
//                            betLoses.add(BetFragment.MATCH_WINNER);
//                        }
//                        if (scoreA.equals(realScoreA) && scoreB.equals(realScoreB)) {
//                            betWins.add(BetFragment.SCORE);
//                            p += 3;
//                        } else {
//                            betLoses.add(BetFragment.SCORE);
//                        }
//                        points = Integer.valueOf(p);
//                    } else {
//                        closed = Boolean.FALSE;
//                    }
//                    break;
//                    
//                case PRESENCE_IN_ROUND:
//                    teamA = bet.getTeamA();
//                    teamB = bet.getTeamB();
//                    betFragments.add(BetFragment.TEAM_A);
//                    betFragments.add(BetFragment.TEAM_B);
//                    if (teamsByRound.get(round) != null && !teamsByRound.get(round).isEmpty()) {
//                        if (roundTeamsDefined.get(round).booleanValue()) {
//                            closed = Boolean.TRUE;
//                        } else {
//                            closed = Boolean.FALSE;
//                        }
//                        int p = 0;
//                        if (teamsByRound.get(round).contains(teamA)) {
//                            betWins.add(BetFragment.TEAM_A);
//                            p += getPointsForTeamInRound(round);
//                        } else {
//                            if (roundTeamsDefined.get(round).booleanValue()) {
//                                betLoses.add(BetFragment.TEAM_A);
//                            } else if (!teamsByRound.get(lastRoundWithTeamsDefined).contains(teamA)) {
//                                betLoses.add(BetFragment.TEAM_A);
//                            }
//                        }
//                        if (teamsByRound.get(round).contains(teamB)) {
//                            betWins.add(BetFragment.TEAM_B);
//                            p += getPointsForTeamInRound(round);
//                        } else {
//                            if (roundTeamsDefined.get(round).booleanValue()) {
//                                betLoses.add(BetFragment.TEAM_B);
//                            } else if (!teamsByRound.get(lastRoundWithTeamsDefined).contains(teamB)) {
//                                betLoses.add(BetFragment.TEAM_B);
//                            }
//                        }
//                        points = Integer.valueOf(p);
//                    } else {
//                        closed = Boolean.FALSE;
//                        if (!teamsByRound.get(lastRoundWithTeamsDefined).contains(teamA)) {
//                            betLoses.add(BetFragment.TEAM_A);
//                        }
//                        if (!teamsByRound.get(lastRoundWithTeamsDefined).contains(teamB)) {
//                            betLoses.add(BetFragment.TEAM_B);
//                        }
//                    }
//                    break;
//                    
//                case FINAL:
//                    teamA = bet.getTeamA();
//                    teamB = bet.getTeamB();
//                    matchWinner = bet.getMatchWinner();
//                    betFragments.add(BetFragment.TEAM_A);
//                    betFragments.add(BetFragment.TEAM_B);
//                    betFragments.add(BetFragment.MATCH_WINNER);
//                    if (teamsByRound.get(round) != null && !teamsByRound.get(round).isEmpty()) {
//                        if (match.isScoreDefined()) {
//                            closed = Boolean.TRUE;
//                        } else {
//                            closed = Boolean.FALSE;
//                        }
//                        int p = 0;
//                        boolean wonTeams = true;
//                        if (teamsByRound.get(round).contains(teamA)) {
//                            betWins.add(BetFragment.TEAM_A);
//                            p += getPointsForTeamInRound(round);
//                        } else {
//                            if (roundTeamsDefined.get(round).booleanValue()) {
//                                betLoses.add(BetFragment.TEAM_A);
//                                wonTeams = false;
//                            } else if (!teamsByRound.get(lastRoundWithTeamsDefined).contains(teamA)) {
//                                betLoses.add(BetFragment.TEAM_A);
//                                wonTeams = false;
//                            }
//                        }
//                        if (teamsByRound.get(round).contains(teamB)) {
//                            betWins.add(BetFragment.TEAM_B);
//                            p += getPointsForTeamInRound(round);
//                        } else {
//                            if (roundTeamsDefined.get(round).booleanValue()) {
//                                betLoses.add(BetFragment.TEAM_B);
//                                wonTeams = false;
//                            } else if (!teamsByRound.get(lastRoundWithTeamsDefined).contains(teamB)) {
//                                betLoses.add(BetFragment.TEAM_B);
//                                wonTeams = false;
//                            }
//                        }
//                        if (roundTeamsDefined.get(round).booleanValue()) {
//                            if (!wonTeams) {
//                                betLoses.add(BetFragment.MATCH_WINNER);
//                            } else if (match.isScoreDefined()) {
//                                if (matchWinner.equals(realMatchWinner)) {
//                                    betWins.add(BetFragment.MATCH_WINNER);
//                                    p += 50;
//                                } else {
//                                    betLoses.add(BetFragment.MATCH_WINNER);
//                                }
//                            }
//                        }
//                        points = Integer.valueOf(p);
//                    } else {
//                        closed = Boolean.FALSE;
//                        if (!teamsByRound.get(lastRoundWithTeamsDefined).contains(teamA)) {
//                            betLoses.add(BetFragment.TEAM_A);
//                            if (bet.getMatchWinner().equals(MatchWinner.TEAM_A)) {
//                                betLoses.add(BetFragment.MATCH_WINNER);
//                            }
//                        }
//                        if (!teamsByRound.get(lastRoundWithTeamsDefined).contains(teamB)) {
//                            betLoses.add(BetFragment.TEAM_B);
//                            if (bet.getMatchWinner().equals(MatchWinner.TEAM_B)) {
//                                betLoses.add(BetFragment.MATCH_WINNER);
//                            }
//                        }
//                    }
//                    break;
//                    
//            }
//
//            if (points != null) {
//                if (round.equals(Round.GROUP_STAGE)) {
//                    groupStagePoints += points.intValue();
//                }
//                totalPoints += points.intValue();
//            }
//            
//            final BetResult betResult =
//                new BetResult(
//                        round, date, name, teamA, teamB, score, realScore, matchWinner, realMatchWinner, 
//                        closed, betFragments, betWins, betLoses, points);
//            betResults.add(betResult);
//            
//        }
//
//        
//        return new BetResults(ownerName, Integer.valueOf(groupStagePoints), Integer.valueOf(totalPoints), betResults);
//        
//    }
//    
//
//    
//    private static MatchWinner computeMatchWinner(final Integer scoreA, final Integer scoreB) {
//        if (scoreA.intValue() == scoreB.intValue()) {
//            return MatchWinner.DRAW;
//        }
//        if (scoreA.intValue() > scoreB.intValue()) {
//            return MatchWinner.TEAM_A;
//        }
//        return MatchWinner.TEAM_B;
//    }
//
//    
//    
//    private static int getPointsForTeamInRound(final Round round) {
//        switch (round) {
//            case GROUP_STAGE:
//                return 0;
//            case ROUND_OF_16:
//                return 5;
//            case QUARTER_FINALS:
//                return 15;
//            case SEMI_FINALS:
//                return 25;
//            case FINAL:
//                return 35; 
//        }
//        return 0;
//    }

    
    
    
  @Transactional
  public void deleteAllBetsForOwner(final String ownerName) {
  }


  
  
  @Transactional
  public BetResults computeBetResults(final String ownerName) {
      return null;
  }

  
  
  
  @Transactional
  public Scoreboard computeScoreboard() {
      return null;
  }
    
}
